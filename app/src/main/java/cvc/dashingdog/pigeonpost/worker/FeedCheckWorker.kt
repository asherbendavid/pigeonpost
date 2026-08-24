package cvc.dashingdog.pigeonpost.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import cvc.dashingdog.pigeonpost.data.BloggerApi
import cvc.dashingdog.pigeonpost.data.FeedRepository
import cvc.dashingdog.pigeonpost.data.FeedStore
import cvc.dashingdog.pigeonpost.data.SettingsStore
import cvc.dashingdog.pigeonpost.notification.NotificationHelper

class FeedCheckWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val repository = FeedRepository(
            BloggerApi.create(),
            FeedStore(applicationContext),
            SettingsStore(applicationContext)
        )

        val result = runCatching { repository.checkForUpdates() }
            .recoverCatching { repository.checkForUpdates() }
            .getOrNull()

        if (result != null && result.newItems.isNotEmpty()) {
            NotificationHelper.notifyNewItems(applicationContext, result.newItems)
        }

        return Result.success()
    }
}