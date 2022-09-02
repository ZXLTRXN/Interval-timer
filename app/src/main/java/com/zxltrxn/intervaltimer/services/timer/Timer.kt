package com.zxltrxn.intervaltimer.services.timer

interface Timer {
    fun start(
        timeInSeconds: Long,
        onTick: () -> Unit,
        onComplete: () -> Unit,
        withDelay: Long = 0L,
        afterDelay: () -> Unit = {}
    )

    fun stop()
}