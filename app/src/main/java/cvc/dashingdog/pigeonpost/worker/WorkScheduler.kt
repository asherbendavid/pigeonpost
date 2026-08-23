package cvc.dashingdog.pigeonpost.worker

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object WorkScheduler {

    private const val WORK_NAME = "pigeonpost_feed_check"

    fun schedule(workManager: WorkManager, intervalHours: Long = 48) {
        val request = PeriodicWorkRequestBuilder<FeedCheckWorker>(
            intervalHours, TimeUnit.HOURS
        ).build()

        workManager.enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE, // lets a future Settings change reschedule cleanly
            request
        )
    }
}