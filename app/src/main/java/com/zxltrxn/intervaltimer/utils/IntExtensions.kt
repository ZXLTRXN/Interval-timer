package com.zxltrxn.intervaltimer.utils

import android.content.Context
import com.zxltrxn.intervaltimer.R

fun Int.secondsToTime(context: Context): String {
    val hours: Int = this / 3600
    val tmp: Int = this % 3600
    val minutes: Int = tmp / 60
    val seconds: Int = tmp % 60
    val padTo2: (Int) -> String = { it.toString().padStart(2, '0') }
    return context.getString(
        R.string.time_representation,
        padTo2(hours),
        padTo2(minutes),
        padTo2(seconds)
    )
}