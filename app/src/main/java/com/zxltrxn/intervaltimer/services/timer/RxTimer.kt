package com.zxltrxn.intervaltimer.services.timer

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RxTimer @Inject constructor() {
    private lateinit var timer: Disposable

    fun start(
        timeInSeconds: Long,
        withDelay: Long = 0L,
        onTick: (Long) -> Unit,
        onComplete: () -> Unit
    ) {
        if (this::timer.isInitialized) stop()
        timer = Observable
            .interval(withDelay, TICK, TimeUnit.MILLISECONDS)
            .take(timeInSeconds)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext(onTick)
            .doOnComplete(onComplete)
            .subscribe()
    }

    fun stop() {
        timer.dispose()
    }

    private companion object {
        const val TICK: Long = 1000
    }
}