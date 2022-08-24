package com.zxltrxn.intervaltimer.services.timer.model

sealed interface TimerState {
    object Initialized : TimerState
    object Started : TimerState
    object Paused : TimerState
    data class PeriodEnded(val period: Period): TimerState
    object Stopped : TimerState
}