package com.zxltrxn.intervaltimer.services.di

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.zxltrxn.intervaltimer.MainActivity
import com.zxltrxn.intervaltimer.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent

@Module
@InstallIn(ServiceComponent::class)
class ServiceModule {
    @Provides
    fun getNotificationManager(@ServiceContext context: Context): NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    @Provides
    @MessageNotification
    fun getNotificationBuilder(
        @ServiceContext context: Context,
        contentIntent: PendingIntent
    ): NotificationCompat.Builder =
        NotificationCompat.Builder(context, CHANNEL_ID)
//            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentIntent(contentIntent)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

    @Provides
    @SoundNotification
    fun getSoundNotificationBuilder(
        @ServiceContext context: Context,
    ): NotificationCompat.Builder =
        NotificationCompat.Builder(context, SOUND_CHANNEL_ID)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

    @Provides
    fun getContentIntent(@ServiceContext context: Context): PendingIntent =
        PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )

    @Provides
    fun getAudioAttributes(): AudioAttributes =
        AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()

    @Provides
    @RequiresApi(Build.VERSION_CODES.O)
    @MessageChannel
    fun createNotificationChannel(): NotificationChannel {
        return NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply { description = CHANNEL_DESCRIPTION }
    }

    @Provides
    @RequiresApi(Build.VERSION_CODES.O)
    @SoundChannel
    fun createSoundChannel(audioAttr: AudioAttributes): NotificationChannel {
        return NotificationChannel(
            SOUND_CHANNEL_ID,
            SOUND_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = SOUND_CHANNEL_DESCRIPTION
            setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), audioAttr)
        }
    }

    private companion object {
        const val SOUND_CHANNEL_ID = "IntervalTimerSoundChannel"
        const val SOUND_CHANNEL_NAME = "IntervalTimerSoundChannelName"
        const val SOUND_CHANNEL_DESCRIPTION = "IntervalTimerSoundChannelDescription"
        const val CHANNEL_ID = "IntervalTimerChannel"
        const val CHANNEL_NAME = "IntervalTimerChannelName"
        const val CHANNEL_DESCRIPTION = "IntervalTimerChannelDescription"
    }
}