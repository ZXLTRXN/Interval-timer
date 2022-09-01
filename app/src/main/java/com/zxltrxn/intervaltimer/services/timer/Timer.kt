package com.zxltrxn.intervaltimer.services.timer

interface Timer {
    fun start(
        timeInSeconds: Long,
        withDelay: Long = 0L,
        onTick: () -> Unit,
        onComplete: () -> Unit,
        afterDelay: () -> Unit = {}
    )

    fun stop()
}