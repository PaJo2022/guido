package com.innoappsai.guido.workers

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.dhandadekho.mobile.utils.Resource
import com.innoappsai.guido.R
import com.innoappsai.guido.data.file.FileRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UploadWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val fileRepository: FileRepository
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "UPLOAD WORKER"
        private const val NOTIFICATION_ID = 1
        private const val PUSH_NOTIFICATION_ID = 1
        private const val NOTIFICATION_CHANNEL_NAME = "FILE UPLOAD WORKER"
        const val FILE_URI = "FILE_URI"
        const val FOLDER_NAME = "FOLDER_NAME"
        const val OUTPUT_NAME = "OUTPUT_NAME"
    }


    override suspend fun doWork(): Result {
        return try {
            // Retrieve the list of byte arrays from input data
            val fileListAsArray = inputData.getStringArray(FILE_URI)
            val outFileName = inputData.getString(OUTPUT_NAME) ?: return Result.failure()
            val folderName = inputData.getString("FOLDER_NAME") ?: "DEFAULT_FOLDER"
            setForeground(createForegroundInfo())
            if (fileListAsArray.isNullOrEmpty()) {
                val outputData = Data.Builder()
                    .putStringArray("uploadedUrls", emptyArray())
                    .build()

                return Result.success(outputData)
            }
            val uploadedUrls = mutableListOf<String>()
            // Perform the upload for each byte array and get the URL
            for (byteArray in fileListAsArray) {
                val uri = Uri.parse(byteArray)
                val fileArray = context.contentResolver.openInputStream(uri)?.use {
                    it.readBytes()
                }
                fileArray?.let {
                    val fileUrl = fileRepository.storeImageToServer(it, folderName)
                    if (fileUrl is Resource.Success) {
                        uploadedUrls.add(fileUrl.data.toString())
                    }
                }


            }


            // Store the list of uploaded URLs in output data
            val outputData = Data.Builder()
                .putStringArray(outFileName, uploadedUrls.toTypedArray())
                .build()

            Result.success(outputData)
        } catch (e: Exception) {
            e.printStackTrace()
            sendPushNotification()
            Result.failure()
        }
    }

    private fun sendPushNotification() {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager




        // Create a notification channel (required for Android 8.0 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }

        // Build the notification
        val notificationBuilder = NotificationCompat.Builder(context,
            NOTIFICATION_CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_website)
            .setContentTitle("Error")
            .setContentText("Please Try Again")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(false)

        // Show the notification
        notificationManager.notify(PUSH_NOTIFICATION_ID, notificationBuilder.build())
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
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }



}
