package com.zxltrxn.intervaltimer.services.timer

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RxTimer @Inject constructor() {
    private var timer: Disposable? = null

    fun start(
        timeInSeconds: Long,
        withDelay: Long = 0L,
        onTick: () -> Unit,
        onComplete: () -> Unit,
        afterDelay: () -> Unit = { }
    ) {
        stop()
        timer = Observable.just(Unit)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .delay(withDelay, TimeUnit.MILLISECONDS)
            .doOnNext { afterDelay() }
            .flatMap {
                Observable.interval(TICK, TimeUnit.MILLISECONDS)
                    .take(timeInSeconds)
                    .doOnNext { onTick() }
                    .doOnComplete { onComplete() }
            }.subscribe()
    }

    fun stop() {
        timer?.dispose()
    }

    private companion object {
        const val TICK: Long = 1000
    }
}