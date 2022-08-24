package com.zxltrxn.intervaltimer.services.timer.model

import android.os.Parcelable
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
            if (field is Period.Rest) passedCycles++
            field = value
        }

    fun next(): Period? {
        if (cycles == passedCycles) {
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

sealed class Period(val time: Int) {
    data class Preparation(val t: Int) : Period(t)
    data class Work(val t: Int) : Period(t)
    data class Rest(val t: Int) : Period(t)
}



