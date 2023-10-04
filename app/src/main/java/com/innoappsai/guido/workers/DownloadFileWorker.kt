package com.innoappsai.guido.workers

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.innoappsai.guido.workers.UploadWorker.Companion.FILE_URI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class DownloadImageWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val imageUrl = inputData.getString(KEY_IMAGE_URL)
        val cacheFileName = inputData.getString(KEY_CACHE_FILE_NAME)

        if (imageUrl.isNullOrEmpty() || cacheFileName.isNullOrEmpty()) {
            return@withContext Result.failure()
        }

        try {
            val filesDir = applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            val cacheFile = File(filesDir, cacheFileName)

            val url = URL(imageUrl)
            val connection = url.openConnection()
            val inputStream = connection.getInputStream()
            val outputStream = FileOutputStream(cacheFile)

            val buffer = ByteArray(1024)
            var bytesRead = inputStream.read(buffer)
            while (bytesRead != -1) {
                outputStream.write(buffer, 0, bytesRead)
                bytesRead = inputStream.read(buffer)
            }

            inputStream.close()
            outputStream.close()
            val uri = FileProvider.getUriForFile(
                applicationContext,
                applicationContext.packageName + ".provider",
                cacheFile
            )
            val outputData = Data.Builder()
                .putString(UploadWorker.OUTPUT_NAME, "PLACE_MAP_IMAGE_FILES")
                .putStringArray(FILE_URI, arrayOf(uri.toString()))
                .putString(UploadWorker.FOLDER_NAME, "places_map_static_image")
                .build()

            return@withContext Result.success(outputData)
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext Result.failure()
        }
    }

    companion object {
        const val KEY_IMAGE_URL = "image_url"
        const val KEY_CACHE_FILE_NAME = "cache_file_name"
        const val DOWNLOAD_FILE_URI = "DOWNLOAD_FILE_URI"
    }
}
