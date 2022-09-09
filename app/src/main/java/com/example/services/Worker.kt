package com.example.services

import android.accounts.NetworkErrorException
import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.net.SocketException
import java.util.concurrent.ExecutionException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class Worker(
    private val context: Context,
    private val params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val urlToDownload = inputData.getString(DOWNLOAD_URL_KEY)!!
        Log.d("Worker", "Work started")
        return when (downloadFile(urlToDownload, context)) {
            "SUCCESS" -> Result.success()
            "RETRY" -> Result.retry()
            else -> Result.failure()
        }
    }

    private suspend fun downloadFile(link: String, context: Context): String {
        return suspendCoroutine { continuation ->
            CoroutineScope(Dispatchers.IO).launch {
                val folder = context.getExternalFilesDir("testFolder")
                val file = File(folder, link.substringAfterLast("/"))
                try {
                    file.outputStream().use { outputStream ->
                        Network.api
                            .getFile(link)
                            .byteStream()
                            .use { inputStream ->
                                inputStream.copyTo(outputStream)
                            }
                    }
                    continuation.resume("SUCCESS")
                } catch (t: Throwable) {
                    Log.d("Worker", "Error download", t)
                    if (t.message == "Connection reset") {
                        continuation.resume("RETRY")
                    } else {
                        continuation.resumeWithException(t)
                    }
                    file.delete()
                }
            }
        }
    }

    companion object {
        const val DOWNLOAD_URL_KEY = "download url"
        const val DOWNLOAD_WORK_ID = "download work"
    }
}