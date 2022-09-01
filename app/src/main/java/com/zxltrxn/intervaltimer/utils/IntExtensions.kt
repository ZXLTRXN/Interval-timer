package com.zxltrxn.intervaltimer.utils

import android.content.Context
import com.zxltrxn.intervaltimer.R

fun Int.secondsToTime(context: Context): String {
    val hours: Int = this / 3600
    val tmp: Int = this % 3600
    val minutes: Int = tmp / 60
    val seconds: Int = tmp % 60

    return context.getString(
        R.string.time_representation,
        hours.padTo2DigitsString(),
        minutes.padTo2DigitsString(),
        seconds.padTo2DigitsString()
    )
}

fun Int.padTo2DigitsString(): String {
    if (this !in 0..99) throw IllegalArgumentException("Only 0..99 available for this representation")
    return this.toString().padStart(2, '0')
}