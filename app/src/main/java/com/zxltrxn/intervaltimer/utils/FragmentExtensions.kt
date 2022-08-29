package com.zxltrxn.intervaltimer.utils

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.zxltrxn.intervaltimer.services.timer.TimerService
import com.zxltrxn.intervaltimer.services.timer.model.TimerCommand

fun Fragment.sendCommandToTimer(command: TimerCommand) {
    ContextCompat.startForegroundService(
        requireActivity(),
        Intent(requireActivity(), TimerService::class.java)
            .putExtra(TimerService.SERVICE_COMMAND, command as Parcelable)
    )
}