package com.zxltrxn.intervaltimer.services.timer.model

import androidx.annotation.ColorInt
import androidx.annotation.StringRes

data class PeriodResource(
    @StringRes val runningTitle: Int,
    @StringRes val endedTitle: Int,
    @ColorInt val color: Int,
)