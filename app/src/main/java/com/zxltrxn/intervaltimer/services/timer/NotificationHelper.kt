package com.zxltrxn.intervaltimer.services.timer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.zxltrxn.intervaltimer.services.di.SilentChannel
import com.zxltrxn.intervaltimer.services.di.SilentNotification
import com.zxltrxn.intervaltimer.services.di.SoundChannel
import com.zxltrxn.intervaltimer.services.di.SoundNotification
import com.zxltrxn.intervaltimer.services.timer.model.NotificationData
import javax.inject.Inject


class NotificationHelper @Inject constructor(
    private val manager: NotificationManager,
    @SilentNotification private val silentNotificationBuilder: NotificationCompat.Builder,
    @SoundNotification private val soundNotificationBuilder: NotificationCompat.Builder,
    @SilentChannel private val silentChannel: NotificationChannel,
    @SoundChannel private val soundChannel: NotificationChannel
) {
    fun getNotification(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannels(listOf(silentChannel, soundChannel))
        }
        return silentNotificationBuilder.build()
    }

    fun updateNotification(data: NotificationData) {
        val builder: NotificationCompat.Builder =
            if (data.withSound) soundNotificationBuilder else silentNotificationBuilder
        builder.setContentTitle(data.title)
            .setColor(data.titleColor)
            .setContentText(data.message)
        manager.notify(NOTIFICATION_ID, builder.build())
    }

    companion object {
        const val NOTIFICATION_ID = 99
    }
}