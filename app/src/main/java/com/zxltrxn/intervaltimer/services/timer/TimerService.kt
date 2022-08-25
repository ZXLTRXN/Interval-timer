package com.zxltrxn.intervaltimer.services.timer

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.zxltrxn.intervaltimer.R
import com.zxltrxn.intervaltimer.services.timer.model.Period
import com.zxltrxn.intervaltimer.services.timer.model.TimePeriods
import com.zxltrxn.intervaltimer.services.timer.model.TimerCommand
import com.zxltrxn.intervaltimer.services.timer.model.TimerState
import com.zxltrxn.intervaltimer.utils.secondsToTime
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Single
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@AndroidEntryPoint
class TimerService : Service() {
    @Inject
    lateinit var helper: NotificationHelper

    @Inject
    lateinit var timer: RxTimer

    private lateinit var serviceState: TimerState
    private lateinit var periods: TimePeriods
    private var remainingTime: Int = 0

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        intent?.extras?.run {
            when (val command = getParcelable<TimerCommand>(SERVICE_COMMAND) as TimerCommand) {
                is TimerCommand.Start -> startTimer(command.periods)
                is TimerCommand.Pause -> pauseTimer()
                is TimerCommand.Continue -> continueTimer()
                is TimerCommand.Stop -> stopService()
            }
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.stop()
    }

    private fun startTimer(periods: TimePeriods) {
        val firstPeriod: Period = periods.next() ?: return
        serviceState = TimerState.Started(firstPeriod)
        this.periods = periods
        remainingTime = firstPeriod.time
        startForeground(NotificationHelper.NOTIFICATION_ID, helper.getNotification())
        broadcastUpdate()
        timer.start(
            timeInSeconds = remainingTime.toLong(),
            withDelay = FIRST_DELAY,
            onTick = this::onTick,
            onComplete = this::updatePeriod
        )
    }

    private fun pauseTimer() {
        if (!this::serviceState.isInitialized || serviceState !is TimerState.Started) return
        serviceState = TimerState.Paused(serviceState.period)
        timer.stop()
        broadcastUpdate()
    }

    private fun continueTimer() {
        if (!this::serviceState.isInitialized || serviceState !is TimerState.Paused) return
        serviceState = TimerState.Started(serviceState.period)
        timer.start(
            timeInSeconds = remainingTime.toLong(),
            onTick = this::onTick,
            onComplete = this::updatePeriod
        )
    }

    private fun updatePeriod() {
        serviceState = TimerState.PeriodEnded(serviceState.period)
        broadcastUpdate()
        val nextPeriod: Period? = periods.next()
        if (nextPeriod != null) {
            remainingTime = nextPeriod.time
            serviceState = TimerState.Started(nextPeriod)
            timer.start(
                timeInSeconds = remainingTime.toLong(),
                withDelay = AFTER_PERIOD_DELAY,
                onTick = this::onTick,
                onComplete = this::updatePeriod,
                afterDelay = this::broadcastUpdate
            )
        } else {
//            Single.just(1)
//                .delay(AFTER_PERIOD_DELAY, TimeUnit.MILLISECONDS)
//                .doOnSuccess { stopService() }
            stopService()
        }
    }

    private fun onTick() {
        remainingTime--
        broadcastUpdate()
    }

    private fun broadcastUpdate() {
        val string = when (val state = serviceState) {
            is TimerState.Started -> {
                sendBroadcast(Intent(TIMER_ACTION).putExtra(REMAINING_TIME, remainingTime))
                remainingTime.secondsToTime(this)
            }
            is TimerState.PeriodEnded -> {
                sendBroadcast(Intent(TIMER_ACTION).putExtra(REMAINING_TIME, remainingTime))
                remainingTime.secondsToTime(this)
            }
            is TimerState.Paused -> getString(R.string.get_back)
        }
        helper.updateNotification(string)
    }

    private fun stopService() {
        if (!this::serviceState.isInitialized) return
        stopForeground(true)
        stopSelf()

    }

    companion object {
        const val SERVICE_COMMAND = "TimerCommand"
        const val TIMER_ACTION = "TimerAction"
        const val REMAINING_TIME = "RemainingTime"
        private const val AFTER_PERIOD_DELAY =
            2000L // for the user to see 00:00 (time ended) and sound
        private const val FIRST_DELAY = 700L // for the user to see the starting number
    }
}