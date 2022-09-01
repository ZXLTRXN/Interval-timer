package com.zxltrxn.intervaltimer.utils

import android.widget.NumberPicker

fun NumberPicker.initialize(displayedValues: List<String>, maxValue: Int, minValue: Int = 0) {
    this.displayedValues = displayedValues.toTypedArray()
    this.minValue = minValue
    this.maxValue = maxValue
}