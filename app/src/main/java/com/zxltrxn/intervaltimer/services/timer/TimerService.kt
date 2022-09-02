package com.zxltrxn.intervaltimer.services.timer

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.zxltrxn.intervaltimer.R
import com.zxltrxn.intervaltimer.WrongCommandException
import com.zxltrxn.intervaltimer.WrongInputTimeException
import com.zxltrxn.intervaltimer.services.timer.model.NotificationData
import com.zxltrxn.intervaltimer.services.timer.model.Period
import com.zxltrxn.intervaltimer.services.timer.model.PeriodResource
import com.zxltrxn.intervaltimer.services.timer.model.TimePeriods
import com.zxltrxn.intervaltimer.services.timer.model.TimerCommand
import com.zxltrxn.intervaltimer.services.timer.model.TimerState
import com.zxltrxn.intervaltimer.utils.secondsToTime
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class TimerService : Service() {
    @Inject
    lateinit var helper: NotificationHelper

    @Inject
    lateinit var timer: Timer

    private var serviceState: TimerState? = null
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
        val firstPeriod: Period = periods.next()
            ?: throw WrongInputTimeException("Unable to get first period while starting timer")
        serviceState = TimerState.Started(firstPeriod)
        this.periods = periods
        remainingTime = firstPeriod.time
        startForeground(NotificationHelper.NOTIFICATION_ID, helper.getNotification())
        broadcastUpdate()
        timer.start(
            timeInSeconds = remainingTime.toLong(),
            onTick = this::onTick,
            onComplete = this::updatePeriod
        )
    }

    private fun pauseTimer() {
        val state: TimerState =
            serviceState ?: throw WrongCommandException("Pause command unavailable before start")
        if (state is TimerState.Paused) return
        if (state is TimerState.PeriodEnded) {
            periods.next()?.let { period ->
                serviceState = TimerState.Paused(period)
                remainingTime = period.time
            } ?: stopService(AFTER_PERIOD_DELAY)
        } else {
            serviceState = TimerState.Paused(state.period)
        }
        timer.stop()
        broadcastUpdate()
    }

    private fun continueTimer() {
        val state: TimerState =
            serviceState ?: throw WrongCommandException("Continue command unavailable before start")
        if (state !is TimerState.Paused) return
        serviceState = TimerState.Started(state.period)
        timer.start(
            timeInSeconds = remainingTime.toLong(),
            onTick = this::onTick,
            onComplete = this::updatePeriod
        )
    }

    private fun stopService(withDelay: Long = 0) {
        if (serviceState == null) throw WrongCommandException("Stop command unavailable before start")
        timer.stop()
        Single.just(Unit)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .delay(withDelay, TimeUnit.MILLISECONDS)
            .doOnSuccess {
                stopForeground(true)
                stopSelf()
            }.subscribe()
    }

    private fun updatePeriod() {
        val state: TimerState = serviceState ?: return
        serviceState = TimerState.PeriodEnded(state.period)
        broadcastUpdate()
        val nextPeriod: Period? = periods.next()
        if (nextPeriod != null) {
            remainingTime = nextPeriod.time
            serviceState = TimerState.Started(nextPeriod)
            timer.start(
                timeInSeconds = remainingTime.toLong(),
                onTick = this::onTick,
                onComplete = this::updatePeriod,
                withDelay = AFTER_PERIOD_DELAY,
                afterDelay = this::broadcastUpdate
            )
        } else stopService(AFTER_PERIOD_DELAY)
    }

    private fun onTick() {
        remainingTime--
        broadcastUpdate()
    }

    private fun broadcastUpdate() {
        val state: TimerState = serviceState
            ?: throw IllegalStateException("Unexpected serviceState == null in broadcastUpdate")
        val res: PeriodResource = state.period.getPeriodResource()
        val notificationData: NotificationData = when (state) {
            is TimerState.Started -> {
                sendBroadcast(Intent(TIMER_ACTION).putExtra(REMAINING_TIME, remainingTime))
                NotificationData(
                    title = getString(res.runningTitle),
                    titleColor = res.color,
                    message = remainingTime.secondsToTime(this)
                )
            }
            is TimerState.PeriodEnded -> {
                sendBroadcast(Intent(TIMER_ACTION).putExtra(REMAINING_TIME, remainingTime))
                NotificationData(
                    title = getString(res.endedTitle),
                    titleColor = res.color,
                    message = remainingTime.secondsToTime(this),
                    withSound = true
                )
            }
            is TimerState.Paused -> NotificationData(
                title = getString(res.runningTitle),
                titleColor = res.color,
                message = getString(R.string.get_back),
            )
        }
        helper.updateNotification(notificationData)
    }

    companion object {
        const val SERVICE_COMMAND = "TimerCommand"
        const val TIMER_ACTION = "TimerAction"
        const val REMAINING_TIME = "RemainingTime"

        // for the user to see 00:00 (time ended) and sound
        private const val AFTER_PERIOD_DELAY = 2000L
    }
}