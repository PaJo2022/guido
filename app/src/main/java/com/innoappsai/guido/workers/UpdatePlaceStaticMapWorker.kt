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
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.innoappsai.guido.MainActivity
import com.innoappsai.guido.MyApp
import com.innoappsai.guido.R
import com.innoappsai.guido.data.places.PlacesRepository
import com.innoappsai.guido.model.places_backend_dto.PlaceRequestDTO
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UpdatePlaceStaticMapWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val placesRepository: PlacesRepository
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "UPDATE_PLACE_STATIC_MAP_WORKER"
        private const val PUSH_NOTIFICATION_ID = 5
        private const val NOTIFICATION_ID = 6
        private const val NOTIFICATION_CHANNEL_NAME = "PLACE STATIC MAP WORKER"
    }


    override suspend fun doWork(): Result {
        return try {
            // Retrieve the list of byte arrays from input data
            val placeId = inputData.getString("PLACE_ID") ?: return Result.failure()
            val uploadedMapImageUrls = inputData.getStringArray("PLACE_MAP_IMAGE_FILES")
            setForeground(getForegroundInfo(applicationContext))
            val staticMapUrl = uploadedMapImageUrls?.firstOrNull() ?: return Result.failure()

            val isPlaceAdded = placesRepository.updatePlaceStaticMapByPlaceId(placeId, staticMapUrl)
            Result.success()
        } catch (e: Exception) {
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



    override suspend fun getForegroundInfo(): ForegroundInfo {
        return getForegroundInfo(applicationContext)
    }

    private fun getForegroundInfo(context: Context): ForegroundInfo {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                NOTIFICATION_ID,
                createNotification(context, "Syncing", "Syncing Place Data"),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(
                NOTIFICATION_ID,
                createNotification(context, "Syncing", "Syncing Place Data"),
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
