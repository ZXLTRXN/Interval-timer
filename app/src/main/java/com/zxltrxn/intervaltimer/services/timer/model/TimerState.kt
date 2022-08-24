package com.zxltrxn.intervaltimer.services.timer.model

sealed interface TimerState {
    object Initialized : TimerState
    data class Started(val periods: TimePeriods) : TimerState
    object Paused : TimerState
    data class PeriodEnded(val period: Period)
    object Finished : TimerState
    object Stopped : TimerState
}