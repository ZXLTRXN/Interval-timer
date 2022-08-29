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
    fun providesNotificationManager(@ServiceContext context: Context): NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private fun getBaseNotification(
        context: Context,
        channelId: String,
        activityIntent: PendingIntent
    ): NotificationCompat.Builder =
        NotificationCompat.Builder(context, channelId)
            .setContentIntent(activityIntent)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

    @Provides
    @SilentNotification
    fun providesSilentNotificationBuilder(
        @ServiceContext context: Context,
        activityIntent: PendingIntent
    ): NotificationCompat.Builder = getBaseNotification(
        context = context,
        channelId = SILENT_CHANNEL_ID,
        activityIntent = activityIntent
    ).setSound(null)

    @Provides
    @RequiresApi(Build.VERSION_CODES.O)
    @SilentChannel
    fun providesSilentChannel(): NotificationChannel {
        return NotificationChannel(
            SILENT_CHANNEL_ID,
            SILENT_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = SILENT_CHANNEL_DESCRIPTION
            setSound(null, null)
        }
    }

    @Provides
    @SoundNotification
    fun providesSoundNotificationBuilder(
        @ServiceContext context: Context,
        activityIntent: PendingIntent
    ): NotificationCompat.Builder = getBaseNotification(
        context = context,
        channelId = SOUND_CHANNEL_ID,
        activityIntent = activityIntent
    ).setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

    @Provides
    @RequiresApi(Build.VERSION_CODES.O)
    @SoundChannel
    fun providesSoundChannel(audioAttr: AudioAttributes): NotificationChannel {
        return NotificationChannel(
            SOUND_CHANNEL_ID,
            SOUND_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = SOUND_CHANNEL_DESCRIPTION
            setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), audioAttr)
        }
    }

    @Provides
    fun providesActivityIntent(@ServiceContext context: Context): PendingIntent =
        PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )

    @Provides
    fun providesAudioAttributes(): AudioAttributes =
        AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()

    private companion object {
        const val SOUND_CHANNEL_ID = "IntTimerSoundChannel"
        const val SOUND_CHANNEL_NAME = "IntTimerSoundChannelName"
        const val SOUND_CHANNEL_DESCRIPTION = "Interval timer sound channel"
        const val SILENT_CHANNEL_ID = "IntTimerChannel"
        const val SILENT_CHANNEL_NAME = "IntTimerChannelName"
        const val SILENT_CHANNEL_DESCRIPTION = "Interval timer silent channel"
    }
}