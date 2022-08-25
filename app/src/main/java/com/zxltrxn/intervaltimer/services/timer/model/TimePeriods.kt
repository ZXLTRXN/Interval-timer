package com.zxltrxn.intervaltimer.services.timer.model

import android.os.Parcelable
import androidx.annotation.StringRes
import com.zxltrxn.intervaltimer.R
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class TimePeriods(
    private val prepTime: Int,
    private val workTime: Int,
    private val restTime: Int,
    private val cycles: Int,
    private val isLastRestEnabled: Boolean = true
) : Parcelable {
    @IgnoredOnParcel
    private var passedCycles: Int = 0

    @IgnoredOnParcel
    private var currentPeriod: Period? = null
        set(value) {
            if (value is Period.Rest) passedCycles++
            field = value
        }

    fun next(): Period? {
        if (passedCycles >= cycles) {
            currentPeriod = null
            return currentPeriod
        }
        currentPeriod = when (currentPeriod) {
            null -> Period.Preparation(prepTime)
            is Period.Preparation -> Period.Work(workTime)
            is Period.Work -> Period.Rest(restTime)
            is Period.Rest -> Period.Work(workTime)
        }
        return currentPeriod
    }
}

sealed class Period(open val time: Int) {
    data class Preparation(override val time: Int) : Period(time)
    data class Work(override val time: Int) : Period(time)
    data class Rest(override val time: Int) : Period(time)

    fun getPeriodResource(): PeriodResource {
        return when (this) {
            is Work -> PeriodResource(
                running = R.string.work_is_running,
                ended = R.string.work_period_ended
            )
            is Rest -> PeriodResource(
                running = R.string.rest_is_running,
                ended = R.string.rest_period_ended
            )
            is Preparation -> PeriodResource(
                running = R.string.preparation_is_running,
                ended = R.string.preparation_period_ended
            )
        }
    }
}

data class PeriodResource(
    @StringRes val running: Int,
    @StringRes val ended: Int,
//    @ColorRes val color: Int
)



