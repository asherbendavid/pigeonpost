package cvc.dashingdog.pigeonpost.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import cvc.dashingdog.pigeonpost.R
import cvc.dashingdog.pigeonpost.data.FeedItem

object NotificationHelper {

    private const val CHANNEL_ID = "pigeonpost_updates"
    private const val NOTIFICATION_ID = 1001

    fun createChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "New releases",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notifies when a new Android Studio release is posted"
        }
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    fun notifyNewItems(context: Context, newItems: List<FeedItem>) {
        if (newItems.isEmpty()) return

        val contentText = if (newItems.size == 1) {
            newItems.first().title
        } else {
            "${newItems.size} new releases: ${newItems.joinToString(", ") { it.title }}"
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // placeholder, real icon in Phase 6
            .setContentTitle("PigeonPost")
            .setContentText(if (newItems.size == 1) contentText else "${newItems.size} new releases")
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
            .setAutoCancel(true)
            .build()

        // Guard: POST_NOTIFICATIONS may not be granted; NotificationManagerCompat
        // handles the check gracefully on pre-33 devices, but we still catch
        // SecurityException defensively since this must fail silently.
        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
        } catch (e: SecurityException) {
            // No permission — silently skip, per spec (non-critical app)
        }
    }
}