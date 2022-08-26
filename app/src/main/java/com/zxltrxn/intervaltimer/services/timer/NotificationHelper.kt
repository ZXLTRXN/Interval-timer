package com.zxltrxn.intervaltimer.services.timer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.os.Build
import androidx.annotation.ColorInt
import androidx.core.app.NotificationCompat
import javax.inject.Inject


class NotificationHelper @Inject constructor() {
    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var notificationBuilder: NotificationCompat.Builder

    @Inject
    lateinit var contentIntent: PendingIntent

    @Inject
    lateinit var channel: NotificationChannel

    fun getNotification(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(channel)
        }
        return notificationBuilder.build()
    }

    fun updateNotification(title: String, @ColorInt color: Int, text: String? = null) {
        with(notificationBuilder) {
            setContentTitle(title)
            setColor(color)
            text?.let { this.setContentText(it) }
        }
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    companion object {
        const val NOTIFICATION_ID = 99
        const val CHANNEL_ID = "IntervalTimerChannel"
        const val CHANNEL_NAME = "IntervalTimerChannelName"
        const val CHANNEL_DESCRIPTION = "IntervalTimerChannelDescription"
    }
}