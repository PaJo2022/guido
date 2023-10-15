package com.innoappsai.guido.workers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.innoappsai.guido.MainActivity
import com.innoappsai.guido.R
import com.innoappsai.guido.TravelItinerary
import com.innoappsai.guido.auth.repo.user.UserRepository
import com.innoappsai.guido.data.tourData.ChatGptRepository
import com.innoappsai.guido.data.travel_itinerary.ItineraryRepository
import com.innoappsai.guido.db.AppPrefs
import com.innoappsai.guido.model.chatGptModel.ChatGptRequest
import com.innoappsai.guido.model.chatGptModel.Message
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CreateItineraryGeneratorWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val chatGptRepository: ChatGptRepository,
    private val itineraryRepository: ItineraryRepository,
    private val userRepository: UserRepository,
    private val appPrefs: AppPrefs
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "GENERATE_ITINERARY_WORKER"
        private const val PUSH_NOTIFICATION_ID = 10
        private const val NOTIFICATION_ID = 11
        private const val NOTIFICATION_CHANNEL_NAME = "GENERATE ITINERARY"
        const val TAG = "CreateItineraryGeneratorWorker"
        private val _workerState: MutableLiveData<WorkerState> = MutableLiveData()
        val workerState: LiveData<WorkerState> = _workerState

        var itineraryDbId: String? = null

        fun onObserved() {
            _workerState.value = WorkerState.IDLE
        }
    }

    private lateinit var itineraryIdForDB: String


    override suspend fun doWork(): Result {
        return try {
            // Retrieve the list of byte arrays from input data
            val query = inputData.getString("ITINERARY_QUERY") ?: return Result.failure()
            itineraryIdForDB = inputData.getString("ITINERARY_ID") ?: return Result.failure()
            itineraryDbId = itineraryIdForDB
            _workerState.postValue(WorkerState.RUNNING)
            setForeground(getForegroundInfo(applicationContext))
            val userId = appPrefs.userId.toString()
            val currentUser = userRepository.getUserDetails(userId)
            if (currentUser?.dbId == null) {
                sendErrorPushNotification("Please re login again")
                return Result.failure()
            }

            val aiResponse = chatGptRepository.getTourDataAboutTheLandMark(
                userDbId = currentUser.dbId,
                shouldSendEmail = true,
                chatGptRequest = ChatGptRequest(
                    listOf(Message(query, "user"))
                )
            )
            if (aiResponse != null) {
                itineraryRepository.addItinerary(TravelItinerary(itineraryIdForDB, aiResponse))
                sendPushNotificationOnSuccessFullPlaceAdd()
            } else {
                sendErrorPushNotification("Something went wrong please try again!")
            }
            _workerState.postValue(WorkerState.COMPLETE)
            Result.success()
        } catch (e: Exception) {
            _workerState.postValue(WorkerState.FAILED)
            e.printStackTrace()
            sendErrorPushNotification(e.message ?: "Something Went Wrong!")
            Result.failure()
        }
    }

    private fun sendErrorPushNotification(message: String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        // Create a notification channel (required for Android 8.0 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }

        // Build the notification
        val notificationBuilder = NotificationCompat.Builder(
            context,
            NOTIFICATION_CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_website)
            .setContentTitle("Error")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(false)

        // Show the notification
        notificationManager.notify(PUSH_NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun sendPushNotificationOnSuccessFullPlaceAdd() {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val deepLinkIntent = Intent(context, MainActivity::class.java)
        deepLinkIntent.putExtra("ITINERARY_DB_ID", itineraryIdForDB)
        deepLinkIntent.putExtra("DEEPLINK", "PLACE_ITINERARY_SCREEN")
        val requestCode = 0 // You can change this value if needed
        val flags =
            PendingIntent.FLAG_MUTABLE // Use FLAG_UPDATE_CURRENT to update the PendingIntent if it already exists
        val pendingIntent = PendingIntent.getActivity(
            context,
            requestCode,
            deepLinkIntent,
            flags
        )

        // Create a notification channel (required for Android 8.0 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }

        // Build the notification
        val notificationBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_website)
            .setContentTitle("Itinerary is Added")
            .setContentText("Your Itinerary is Added")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        // Show the notification
        notificationManager.notify(PUSH_NOTIFICATION_ID, notificationBuilder.build())
    }


    override suspend fun getForegroundInfo(): ForegroundInfo {
        return getForegroundInfo(applicationContext)
    }

    private fun getForegroundInfo(context: Context): ForegroundInfo {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                NOTIFICATION_ID,
                createNotification(context, "Creating", "Your Itinerary Is Creating"),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(
                NOTIFICATION_ID,
                createNotification(context, "Creating", "Your Itinerary Is Creating"),
            )
        }
    }

    private fun createNotification(
        context: Context,
        title: String,
        description: String
    ): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }
        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(title)
            .setTicker(title)
            .setContentText(description)
            .setSmallIcon(R.drawable.ic_website)
            .setOngoing(true)
            .build()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val importance = NotificationManager.IMPORTANCE_HIGH // Set the importance level
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME, importance
        )
        // Register the notification channel with the system
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
