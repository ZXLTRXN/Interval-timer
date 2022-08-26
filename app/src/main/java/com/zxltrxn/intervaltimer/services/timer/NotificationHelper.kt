package com.zxltrxn.intervaltimer.services.timer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.zxltrxn.intervaltimer.services.di.MessageChannel
import com.zxltrxn.intervaltimer.services.di.MessageNotification
import com.zxltrxn.intervaltimer.services.di.SoundChannel
import com.zxltrxn.intervaltimer.services.di.SoundNotification
import com.zxltrxn.intervaltimer.services.timer.model.NotificationData
import javax.inject.Inject


class NotificationHelper @Inject constructor(
    private val manager: NotificationManager,
    @MessageNotification private val notificationBuilder: NotificationCompat.Builder,
    @SoundNotification private val soundNotificationBuilder: NotificationCompat.Builder,
    @MessageChannel private val channel: NotificationChannel,
    @SoundChannel private val soundChannel: NotificationChannel
) {
    fun getNotification(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            manager.createNotificationChannels(listOf(channel, soundChannel))
            manager.createNotificationChannel(channel)
        }
        return notificationBuilder.build()
    }

    fun updateNotification(data: NotificationData) {
        with(notificationBuilder) {
            setContentTitle(data.title)
            color = data.titleColor
            data.message?.let { this.setContentText(it) }
        }
        manager.notify(NOTIFICATION_ID, notificationBuilder.build())
        if (data.withSound) manager.notify(NOTIFICATION_ID, soundNotificationBuilder.build())
    }

    companion object {
        const val NOTIFICATION_ID = 99
    }
}