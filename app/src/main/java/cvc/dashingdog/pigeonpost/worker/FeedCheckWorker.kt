package cvc.dashingdog.pigeonpost.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import cvc.dashingdog.pigeonpost.data.BloggerApi
import cvc.dashingdog.pigeonpost.data.FeedRepository
import cvc.dashingdog.pigeonpost.data.FeedStore
import cvc.dashingdog.pigeonpost.notification.NotificationHelper

class FeedCheckWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val repository = FeedRepository(BloggerApi.create(), FeedStore(applicationContext))

        // Single inline retry on failure — not WorkManager's backoff, just one
        // immediate second attempt for transient network blips.
        val result = runCatching { repository.checkForUpdates() }
            .recoverCatching { repository.checkForUpdates() }
            .getOrNull()

        if (result != null && result.newItems.isNotEmpty()) {
            NotificationHelper.notifyNewItems(applicationContext, result.newItems)
        }

        // Always success — a failed poll just means the next scheduled run
        // will try again; no need for WorkManager's own retry/backoff.
        return Result.success()
    }
}