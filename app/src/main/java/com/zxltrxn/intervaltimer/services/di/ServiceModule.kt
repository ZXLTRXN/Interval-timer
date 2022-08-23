package com.zxltrxn.intervaltimer.services.di

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.zxltrxn.intervaltimer.MainActivity
import com.zxltrxn.intervaltimer.R
import com.zxltrxn.intervaltimer.services.timer.NotificationHelper
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
    fun getNotificationBuilder(
        @ServiceContext context: Context,
        contentIntent: PendingIntent
    ): NotificationCompat.Builder =
        NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID)
            .setContentTitle(context.getString(R.string.app_name))
            .setSound(null)
            .setContentIntent(contentIntent)
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
    @RequiresApi(Build.VERSION_CODES.O)
    fun createChannel(): NotificationChannel =
        NotificationChannel(
            NotificationHelper.CHANNEL_ID,
            NotificationHelper.CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = NotificationHelper.CHANNEL_DESCRIPTION
            setSound(null, null)
        }
}