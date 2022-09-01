package com.zxltrxn.intervaltimer.utils

import android.os.Build
import android.widget.NumberPicker
import androidx.annotation.RequiresApi

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