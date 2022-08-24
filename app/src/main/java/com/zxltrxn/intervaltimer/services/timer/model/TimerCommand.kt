package com.zxltrxn.intervaltimer.services.timer.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface TimerCommand : Parcelable {
    @Parcelize
    data class Start(val periods: TimePeriods) : TimerCommand

    @Parcelize
    object Pause : TimerCommand

    @Parcelize
    object Continue : TimerCommand

    @Parcelize
    object Stop : TimerCommand
}