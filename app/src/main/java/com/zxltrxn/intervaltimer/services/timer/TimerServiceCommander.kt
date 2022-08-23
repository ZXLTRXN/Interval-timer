package com.zxltrxn.intervaltimer.services.timer

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.core.content.ContextCompat

interface TimerServiceCommander {
    fun sendCommandToService(context: Context, command: TimerState)
}

class TimerServiceCommanderImpl() : TimerServiceCommander {
    override fun sendCommandToService(context: Context, command: TimerState) {
        ContextCompat.startForegroundService(
            context,
            Intent(context, TimerService::class.java)
                .putExtra(TimerService.SERVICE_COMMAND, command as Parcelable)
        )
    }
}