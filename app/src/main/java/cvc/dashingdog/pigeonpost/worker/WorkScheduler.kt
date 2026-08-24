package cvc.dashingdog.pigeonpost.worker

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object WorkScheduler {

    private const val WORK_NAME = "pigeonpost_feed_check"

    fun schedule(workManager: WorkManager, intervalHours: Long) {
        val safeInterval = intervalHours.coerceAtLeast(1) // WorkManager floor is 15 min; hours granularity is fine here
        val request = PeriodicWorkRequestBuilder<FeedCheckWorker>(
            safeInterval, TimeUnit.HOURS
        ).build()

        workManager.enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }
}