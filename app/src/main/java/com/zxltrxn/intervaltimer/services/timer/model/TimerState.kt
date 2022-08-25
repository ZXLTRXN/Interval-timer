package com.zxltrxn.intervaltimer.services.timer.model

sealed class TimerState(open val period: Period) {
    data class Started(override val period: Period) : TimerState(period)
    data class Paused(override val period: Period) : TimerState(period)
    data class PeriodEnded(override val period: Period) : TimerState(period)
}