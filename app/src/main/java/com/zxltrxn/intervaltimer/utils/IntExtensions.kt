package com.zxltrxn.intervaltimer.utils

import android.content.Context
import com.zxltrxn.intervaltimer.R

fun Int.secondsToTime(context: Context): String {
    val hours: Int = this / 3600
    val tmp: Int = this % 3600
    val minutes: Int = tmp / 60
    val seconds: Int = tmp % 60
    return context.getString(R.string.time_representation, hours, minutes, seconds)
}