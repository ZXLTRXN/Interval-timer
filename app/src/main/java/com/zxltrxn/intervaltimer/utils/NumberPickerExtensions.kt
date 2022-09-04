package com.zxltrxn.intervaltimer.utils

import android.os.Build
import android.widget.NumberPicker
import androidx.annotation.RequiresApi
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

fun NumberPicker.initialize(
    displayedValues: List<String>,
    maxValue: Int,
    currentValue: Int = 0,
    minValue: Int = 0
) {
    this.displayedValues = displayedValues.toTypedArray()
    this.minValue = minValue
    this.maxValue = maxValue
    this.value = currentValue
}

@RequiresApi(Build.VERSION_CODES.Q)
fun NumberPicker.initializeUI(textSize: Float) {
    this.textSize = textSize
    selectionDividerHeight = 0
}

fun NumberPicker.observableChangeListener(): Observable<Int> {
    val emitter: BehaviorSubject<Int> = BehaviorSubject.create()
    emitter.onNext(0)
    setOnValueChangedListener { _, _, newValue ->
        emitter.onNext(newValue)
    }
    return emitter
}