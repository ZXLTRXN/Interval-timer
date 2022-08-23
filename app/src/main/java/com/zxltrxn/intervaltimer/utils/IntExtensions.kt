package com.zxltrxn.intervaltimer.utils

fun Int.secondsToTime(pattern: String): String {
    val hours: Int = this / 3600
    val tmp: Int = this % 3600
    val minutes: Int = tmp / 60
    val seconds: Int = tmp % 60
    return pattern.format(hours, minutes, seconds)
}