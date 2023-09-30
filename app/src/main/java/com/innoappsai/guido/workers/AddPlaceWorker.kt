package com.innoappsai.guido.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
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
        private const val NOTIFICATION_CHANNEL_ID = "PaJo App"
        private const val PUSH_NOTIFICATION_ID = 1
        private const val NOTIFICATION_ID = 2
        private const val NOTIFICATION_CHANNEL_NAME = "App Background Work"
    }


    override suspend fun doWork(): Result {
        return try {
            // Retrieve the list of byte arrays from input data
            val uploadedImageUrls = inputData.getStringArray("uploadedUrls")
            val placeDTO = MyApp.placeRequestDTO ?: return Result.failure()

            placeDTO.photos = uploadedImageUrls?.toList()
            setForeground(createForegroundInfo())
            val isPlaceAdded = placesRepository.addPlace(placeDTO)
            if (isPlaceAdded != null) {
                sendPushNotifications(placeDTO.placeName.toString())
            }
            MyApp.imageFileArray = null
            MyApp.placeRequestDTO = null
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    private fun sendPushNotifications(placeName: String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create a notification channel (required for Android 8.0 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }

        // Build the notification
        val notificationBuilder = NotificationCompat.Builder(context, "alarm_channel")
            .setSmallIcon(R.drawable.ic_website)
            .setContentTitle("Adding Place")
            .setContentText("${placeName} is Added")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(false)

        // Show the notification
        notificationManager.notify(PUSH_NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun createForegroundInfo(): ForegroundInfo {

        val title = "TEST"
        val cancel = "TEST"
        // This PendingIntent can be used to cancel the worker
        val intent = WorkManager.getInstance(applicationContext)
            .createCancelPendingIntent(id)

        // Create a Notification channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }

        val notification = NotificationCompat.Builder(applicationContext,
            NOTIFICATION_CHANNEL_ID
        )
            .setContentTitle(title)
            .setTicker(title)
            .setContentText("Uploading")
            .setSmallIcon(R.drawable.ic_website)
            .setOngoing(true)
            // Add the cancel action to the notification which can
            // be used to cancel the worker
            .addAction(android.R.drawable.ic_delete, cancel, intent)
            .build()

        return ForegroundInfo(NOTIFICATION_ID, notification)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val importance = NotificationManager.IMPORTANCE_HIGH // Set the importance level
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME, importance)
        // Customize channel settings if needed
        channel.description = "Your Channel Description" // Optional: Provide a description
        // Configure additional channel properties as needed
        channel.enableLights(true) // Enable notification LED
        channel.lightColor = Color.RED // Set LED color
        channel.enableVibration(true) // Enable vibration
        channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500) // Vibration pattern

        // Register the notification channel with the system
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
