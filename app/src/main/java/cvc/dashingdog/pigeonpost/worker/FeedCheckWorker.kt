package cvc.dashingdog.pigeonpost.worker

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
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
        Log.d(TAG, "FeedCheckWorker started")

        val repository = FeedRepository(BloggerApi.create(), FeedStore(applicationContext))

        val result = runCatching { repository.checkForUpdates() }
            .recoverCatching { repository.checkForUpdates() }
            .getOrNull()

        if (result == null) {
            Log.d(TAG, "FeedCheckWorker: both attempts failed, giving up quietly")
            showDebugToast("PigeonPost check failed (will retry next cycle)")
        } else {
            Log.d(TAG, "FeedCheckWorker: fetched ${result.filteredItems.size} items, ${result.newItems.size} new")
            showDebugToast("PigeonPost checked: ${result.newItems.size} new item(s)")
            if (result.newItems.isNotEmpty()) {
                NotificationHelper.notifyNewItems(applicationContext, result.newItems)
            }
        }

        return Result.success()
    }

    // DEBUG ONLY — remove with the rest of this branch's changes.
    private fun showDebugToast(message: String) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        private const val TAG = "PigeonPostWorker"
    }
}