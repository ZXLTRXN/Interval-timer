package com.zxltrxn.intervaltimer.utils

import android.os.Build
import android.widget.NumberPicker
import androidx.annotation.RequiresApi
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

fun NumberPicker.initialize(displayedValues: List<String>, maxValue: Int, minValue: Int = 0) {
    this.displayedValues = displayedValues.toTypedArray()
    this.minValue = minValue
    this.maxValue = maxValue
}

@RequiresApi(Build.VERSION_CODES.Q)
fun NumberPicker.initializeUI(textSize: Float) {
    this.textSize = textSize
    selectionDividerHeight = 0
}

fun NumberPicker.observableChangeListener(): Observable<Int> {
    val emitter: PublishSubject<Int> = PublishSubject.create()
    setOnValueChangedListener { _, _, newValue ->
        emitter.onNext(newValue)
    }
    return emitter
}