package com.innoappsai.guido.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.innoappsai.guido.MainActivity
import com.innoappsai.guido.MyApp
import com.innoappsai.guido.R
import com.innoappsai.guido.data.places.PlacesRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class AddPlaceWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val placesRepository: PlacesRepository
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "ADD_PLACE_WORKER"
        private const val PUSH_NOTIFICATION_ID = 3
        private const val NOTIFICATION_ID = 4
        private const val NOTIFICATION_CHANNEL_NAME = "PLACE ADD WORKER"
    }


    override suspend fun doWork(): Result {
        return try {
            // Retrieve the list of byte arrays from input data
            val uploadedImageUrls = inputData.getStringArray("IMAGE_FILES")
            val uploadedVideoUrls = inputData.getStringArray("VIDEO_FILES")
            val placeDTO = MyApp.placeRequestDTO ?: return Result.failure()

            placeDTO.photos = uploadedImageUrls?.toList()
            placeDTO.videos = uploadedVideoUrls?.toList()
            setForeground(createForegroundInfo(placeDTO.placeName.toString()))
            val isPlaceAdded = placesRepository.addPlace(placeDTO)
            if (isPlaceAdded != null) {
                sendPushNotificationOnSuccessFullPlaceAdd(placeDTO.placeName.toString(),placeDTO.placeId.toString())
            }
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    private fun sendPushNotificationOnSuccessFullPlaceAdd(placeName: String,placeId : String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager




        // Create a notification channel (required for Android 8.0 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }

        // Build the notification
        val notificationBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_website)
            .setContentTitle("Adding Place")
            .setContentText("${placeName} is Added")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(false)

        // Show the notification
        notificationManager.notify(PUSH_NOTIFICATION_ID, notificationBuilder.build())
    }



    private fun createForegroundInfo(placeName : String): ForegroundInfo {

        // Create a Notification channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }

        val notification = NotificationCompat.Builder(applicationContext,
            NOTIFICATION_CHANNEL_ID
        )
            .setContentTitle("${placeName} is adding")
            .setTicker("${placeName} is adding")
            .setContentText("Adding")
            .setSmallIcon(R.drawable.ic_website)
            .setOngoing(true)
            .build()

        return ForegroundInfo(NOTIFICATION_ID, notification)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val importance = NotificationManager.IMPORTANCE_HIGH // Set the importance level
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME, importance)
        // Register the notification channel with the system
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
