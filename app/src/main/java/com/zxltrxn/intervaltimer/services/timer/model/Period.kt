package com.zxltrxn.intervaltimer.services.timer.model

import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import com.zxltrxn.intervaltimer.R


sealed class Period(open val time: Int) {
    data class Preparation(override val time: Int) : Period(time)
    data class Work(override val time: Int) : Period(time)
    data class Rest(override val time: Int) : Period(time)

    fun getPeriodResource(): PeriodResource {
        return when (this) {
            is Work -> PeriodResource(
                runningTitle = R.string.work_running_title,
                endedTitle = R.string.work_ended_title,
                color = BLUE
            )
            is Rest -> PeriodResource(
                runningTitle = R.string.rest_running_title,
                endedTitle = R.string.rest_ended_title,
                color = GREEN
            )
            is Preparation -> PeriodResource(
                runningTitle = R.string.preparation_running_title,
                endedTitle = R.string.preparation_ended_title,
                color = CYAN
            )
        }
    }

    private companion object {
        @ColorInt
        const val GREEN = -0xff0100
        @ColorInt
        const val BLUE = -0xffff01
        @ColorInt
        const val CYAN = -0xff0001
    }
}

data class PeriodResource(
    @StringRes val runningTitle: Int,
    @StringRes val endedTitle: Int,
    @ColorInt val color: Int,
)