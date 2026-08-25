package cvc.dashingdog.pigeonpost.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import cvc.dashingdog.pigeonpost.MainActivity
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

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("PigeonPost")
            .setContentText(if (newItems.size == 1) contentText else "${newItems.size} new releases")
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
        } catch (_: SecurityException) {
            // No permission — silently skip, per spec (non-critical app)
        }
    }
}