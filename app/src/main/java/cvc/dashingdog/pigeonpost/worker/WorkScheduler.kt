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
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    /**
     * DEBUG ONLY — 15 minutes is WorkManager's enforced minimum for periodic
     * work. Used to confirm the worker actually fires and notifies in the
     * background, without waiting days for the real schedule.
     * Not for release — this whole function should not exist past this branch.
     */
    fun scheduleTestInterval(workManager: WorkManager) {
        val request = PeriodicWorkRequestBuilder<FeedCheckWorker>(
            15, TimeUnit.MINUTES
        ).build()

        workManager.enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }
}