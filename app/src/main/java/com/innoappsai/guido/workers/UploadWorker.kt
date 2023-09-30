package com.innoappsai.guido.workers

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.dhandadekho.mobile.utils.Resource
import com.innoappsai.guido.MyApp
import com.innoappsai.guido.R
import com.innoappsai.guido.data.file.FileRepository
import com.innoappsai.guido.getImageBytes
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@HiltWorker
class UploadWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val fileRepository: FileRepository
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "PaJo App"
        private const val NOTIFICATION_ID = 2
        private const val NOTIFICATION_CHANNEL_NAME = "App Background Work"
        const val FOLDER_NAME = "FOLDER_NAME"
    }


    override suspend fun doWork(): Result {
        return try {
            // Retrieve the list of byte arrays from input data
            withContext(Dispatchers.IO){
                val byteArrayList = MyApp.imageFileArray
                val folderName = inputData.getString("FOLDER_NAME") ?: "DEFAULT_FOLDER"
                if (byteArrayList.isNullOrEmpty()) {
                    val outputData = Data.Builder()
                        .putStringArray("uploadedUrls", emptyArray())
                        .build()

                    return@withContext Result.success(outputData)
                }
                setForeground(createForegroundInfo())
                val uploadedUrls = mutableListOf<String>()
                // Perform the upload for each byte array and get the URL
                for (byteArray in byteArrayList) {
                    val file = File(byteArray.second)
                    val fileArray = getImageBytes(file)
                    val fileUrl = fileRepository.storeImageToServer(fileArray, folderName)
                    if (fileUrl is Resource.Success) {
                        uploadedUrls.add(fileUrl.data.toString())
                    }

                }


                // Store the list of uploaded URLs in output data
                val outputData = Data.Builder()
                    .putStringArray("uploadedUrls", uploadedUrls.toTypedArray())
                    .build()

                Result.success(outputData)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }


    @SuppressLint("RemoteViewLayout")
    private fun createForegroundInfo(): ForegroundInfo {

        val title = "Files Are Uploading"
        // Create a Notification channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }
        // This PendingIntent can be used to cancel the worker
        val intent = WorkManager.getInstance(applicationContext)
            .createCancelPendingIntent(id)



        val notification = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(title)
            .setTicker(title)
            .setContentText("Uploading")
            .setSmallIcon(R.drawable.ic_website)
            .setOngoing(true)
            .build()

        return ForegroundInfo(NOTIFICATION_ID, notification)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val importance = NotificationManager.IMPORTANCE_HIGH // Set the importance level
        val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, importance)
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
