package com.example.services

import android.content.Context
import androidx.work.*
import com.example.services.Worker.Companion.DOWNLOAD_WORK_ID
import java.util.concurrent.TimeUnit

class Repository(
    private val context: Context
) {

    fun startDownload(url: String): Operation {
        val workData = workDataOf(
            Worker.DOWNLOAD_URL_KEY to url
        )

        val workConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresBatteryNotLow(true)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<Worker>()
            .setInputData(workData)
            .setBackoffCriteria(BackoffPolicy.LINEAR, 20, TimeUnit.SECONDS)
            .setConstraints(workConstraints)
            .build()

        return WorkManager.getInstance(context)
            .enqueueUniqueWork(DOWNLOAD_WORK_ID, ExistingWorkPolicy.KEEP, workRequest)
    }

    fun stopDownload() {
        WorkManager.getInstance(context).cancelUniqueWork(DOWNLOAD_WORK_ID)
    }

    companion object {

    }
}