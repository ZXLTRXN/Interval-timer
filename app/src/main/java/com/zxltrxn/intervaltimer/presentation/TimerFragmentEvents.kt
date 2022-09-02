package com.zxltrxn.intervaltimer.presentation

import io.reactivex.rxjava3.core.Observable

class TimerFragmentEvents(
    val hoursChanged: Observable<Int>,
    val minutesChanged: Observable<Int>,
    val secondsChanged: Observable<Int>
)