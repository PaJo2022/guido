package com.innoappsai.guido.workers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
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
        private const val PUSH_NOTIFICATION_ID = 2
        private const val NOTIFICATION_CHANNEL_NAME = "FILE UPLOAD WORKER"
        const val TAG = "UploadWorker"
        const val FILE_URI = "FILE_URI"
        const val FOLDER_NAME = "FOLDER_NAME"
        const val OUTPUT_NAME = "OUTPUT_NAME"
    }


    override suspend fun doWork(): Result {
        return try {
            // Retrieve the list of byte arrays from input data
            val inputDataBuilder = Data.Builder()
            val fileListAsArray = inputData.getStringArray(FILE_URI)
            val staticMapFile = inputData.getStringArray("PLACE_MAP_IMAGE_FILES")
            if (staticMapFile != null) {
                inputDataBuilder.putStringArray("PLACE_MAP_IMAGE_FILES", staticMapFile)
            }
            val outFileName = inputData.getString(OUTPUT_NAME) ?: return Result.failure()
            val folderName = inputData.getString("FOLDER_NAME") ?: "DEFAULT_FOLDER"
            setForeground(getForegroundInfo(applicationContext))
            if (fileListAsArray.isNullOrEmpty()) {
                val outputData = inputDataBuilder.build()
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


            val outputData = inputDataBuilder
                .putStringArray(outFileName, uploadedUrls.toTypedArray())
                .build()
            Result.success(outputData)
        } catch (e: Exception) {
            e.printStackTrace()
            sendPushNotification(e.message ?: "Something Went Wrong!")
            Result.failure()
        }
    }

    private fun sendPushNotification(message : String) {
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
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(false)

        // Show the notification
        notificationManager.notify(PUSH_NOTIFICATION_ID, notificationBuilder.build())
    }


    override suspend fun getForegroundInfo(): ForegroundInfo {
        return getForegroundInfo(applicationContext)
    }

    private fun getForegroundInfo(context: Context) : ForegroundInfo{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                NOTIFICATION_ID,
                createNotification(context,"Uploading","Files Are Uploading"),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(
                NOTIFICATION_ID,
                createNotification(context,"Uploading","Files Are Uploading")
            )
        }
    }

    private fun createNotification(context: Context,title : String,description : String): Notification {
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
    private fun createChannel() {NOTIFICATION_CHANNEL_ID
        val importance = NotificationManager.IMPORTANCE_HIGH // Set the importance level
        val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, importance)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }



}
