package com.example.module

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class BulkDownloadWorker(appContext: Context, workerParams: WorkerParameters)
    : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Create a dedicated folder (e.g., "videos") in external files directory
            val videosDir = File(applicationContext.getExternalFilesDir(null), "videos")
            if (!videosDir.exists()) {
                videosDir.mkdirs()
            }
            // Iterate through each entry in the video map
            for ((key, _) in VideoRepository.videoMap) {
                // Expecting key in format "videoCode_viewType" (e.g., "n0_front")
                val parts = key.split("_")
                if (parts.size < 2) continue
                val videoCode = parts[0]
                val viewType = parts[1]
                val videoUrl = VideoRepository.getCloudinaryUrl(videoCode, viewType)
                if (videoUrl.isEmpty()) continue

                // Create a destination file named like "n0_front.mp4"
                val destinationFile = File(videosDir, "${videoCode}_${viewType}.mp4")
                if (destinationFile.exists()) {
                    // Skip download if already exists
                    continue
                }
                val success = downloadFile(videoUrl, destinationFile)
                if (!success) {
                }
            }
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    private fun downloadFile(urlStr: String, destinationFile: File): Boolean {
        return try {
            val url = URL(urlStr)
            val connection = url.openConnection() as HttpURLConnection
            connection.connect()
            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                return false
            }
            val inputStream = connection.inputStream
            val outputStream = FileOutputStream(destinationFile)
            val buffer = ByteArray(4096)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }
            outputStream.close()
            inputStream.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
