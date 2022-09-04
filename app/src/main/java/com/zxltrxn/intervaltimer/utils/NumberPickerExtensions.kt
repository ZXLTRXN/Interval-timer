package com.zxltrxn.intervaltimer.utils

import android.os.Build
import android.widget.NumberPicker
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData

fun NumberPicker.initialize(
    displayedValues: List<String>,
    maxValue: Int,
    minValue: Int = 0
) {
    this.displayedValues = displayedValues.toTypedArray()
    this.minValue = minValue
    this.maxValue = maxValue
}

@RequiresApi(Build.VERSION_CODES.Q)
fun NumberPicker.initializeUI(textSize: Float) {
    this.textSize = textSize
    selectionDividerHeight = 0
}

fun NumberPicker.bindTwoWay(
    liveData: MutableLiveData<Int>,
    lifecycleOwner: LifecycleOwner
) {
    this.setOnValueChangedListener { _, _, newValue -> liveData.value = newValue }

    liveData.observe(lifecycleOwner) { time ->
        if (this.value == time) return@observe
        this.value = time
    }
}