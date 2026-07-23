package com.dg.fendaice.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import com.dg.fendaice.MainActivity
import com.dg.fendaice.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FendaiceMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.notification?.title
            ?: message.data["title"]
            ?: getString(R.string.notification_default_title)
        val body = message.notification?.body
            ?: message.data["body"]
            ?: getString(R.string.notification_default_body)
        val type = message.data["type"] ?: TYPE_GENERAL

        showNotification(title, body, type)
    }

    private fun showNotification(title: String, body: String, type: String) {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        ensureChannels(manager)

        val channelId = when (type) {
            TYPE_RANKING -> CHANNEL_RANKING
            TYPE_CHALLENGE -> CHANNEL_CHALLENGE
            else -> CHANNEL_GENERAL
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("notification_type", type)
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            type.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val largeIcon = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setLargeIcon(largeIcon)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(body)
                    .setBigContentTitle(title)
                    .setSummaryText(getString(R.string.app_name))
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setColor(0xFF1E88E5.toInt())
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        // Action based on type
        when (type) {
            TYPE_RANKING -> {
                builder.addAction(
                    R.drawable.ic_notification,
                    getString(R.string.notification_action_view_ranking),
                    pendingIntent
                )
            }
            TYPE_CHALLENGE -> {
                builder.addAction(
                    R.drawable.ic_notification,
                    getString(R.string.notification_action_play_now),
                    pendingIntent
                )
            }
            else -> {
                builder.addAction(
                    R.drawable.ic_notification,
                    getString(R.string.notification_action_open),
                    pendingIntent
                )
            }
        }

        manager.notify(System.currentTimeMillis().toInt(), builder.build())
    }

    private fun ensureChannels(manager: NotificationManager) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channels = listOf(
            NotificationChannel(
                CHANNEL_GENERAL,
                getString(R.string.notification_channel_general),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = getString(R.string.notification_channel_general_desc)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 250, 250)
                enableLights(true)
                lightColor = 0xFF1E88E5.toInt()
            },
            NotificationChannel(
                CHANNEL_RANKING,
                getString(R.string.notification_channel_ranking),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = getString(R.string.notification_channel_ranking_desc)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500)
                enableLights(true)
                lightColor = 0xFF1E88E5.toInt()
            },
            NotificationChannel(
                CHANNEL_CHALLENGE,
                getString(R.string.notification_channel_challenge),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = getString(R.string.notification_channel_challenge_desc)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 200, 100, 200, 100, 200)
                enableLights(true)
                lightColor = 0xFF1E88E5.toInt()
            }
        )
        channels.forEach { manager.createNotificationChannel(it) }
    }

    companion object {
        const val CHANNEL_GENERAL = "fendaice_general"
        const val CHANNEL_RANKING = "fendaice_ranking"
        const val CHANNEL_CHALLENGE = "fendaice_challenge"

        const val TYPE_GENERAL = "general"
        const val TYPE_RANKING = "ranking"
        const val TYPE_CHALLENGE = "challenge"
    }
}
