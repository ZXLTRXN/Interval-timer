package com.zxltrxn.intervaltimer.services.timer.model

import android.os.Parcelable
import com.zxltrxn.intervaltimer.WrongInputTimeException
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

    init {
        if (workTime <= 0 || restTime <= 0 || cycles <= 0) {
            throw WrongInputTimeException("Work or rest time, or cycles should be greater than 0")
        }
    }

    fun next(): Period? {
        if (!isLastRestEnabled && currentPeriod is Period.Work && passedCycles == cycles - 1) passedCycles++
        if (passedCycles >= cycles) {
            currentPeriod = null
            return currentPeriod
        }
        currentPeriod = when (currentPeriod) {
            null -> {
                if (prepTime == 0) Period.Work(workTime)
                else Period.Preparation(prepTime)
            }
            is Period.Preparation -> Period.Work(workTime)
            is Period.Work -> Period.Rest(restTime)
            is Period.Rest -> Period.Work(workTime)
        }
        return currentPeriod
    }
}



