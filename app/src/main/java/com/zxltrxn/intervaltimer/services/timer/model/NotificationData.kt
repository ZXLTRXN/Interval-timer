package com.zxltrxn.intervaltimer.services.timer.model

import androidx.annotation.ColorInt

data class NotificationData(
    val title: String,
    @ColorInt val titleColor: Int,
    val message: String? = null,
    val withSound: Boolean = false
)