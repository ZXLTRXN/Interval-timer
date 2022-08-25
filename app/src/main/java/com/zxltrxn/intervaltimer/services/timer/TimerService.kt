package com.zxltrxn.intervaltimer.services.timer

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.service.media.MediaBrowserService
import com.zxltrxn.intervaltimer.R
import com.zxltrxn.intervaltimer.services.timer.model.Period
import com.zxltrxn.intervaltimer.services.timer.model.TimePeriods
import com.zxltrxn.intervaltimer.services.timer.model.TimerCommand
import com.zxltrxn.intervaltimer.services.timer.model.TimerState
import com.zxltrxn.intervaltimer.utils.secondsToTime
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@AndroidEntryPoint
class TimerService : Service() {
    @Inject
    lateinit var helper: NotificationHelper

    @Inject
    lateinit var timer: RxTimer

    private var serviceState: TimerState = TimerState.Initialized
    private var remainingTime: Int = 0
    private var periods: TimePeriods? = null
    private var currentPeriod: Period? = null

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        intent?.extras?.run {
            when (val command = getParcelable<TimerCommand>(SERVICE_COMMAND) as TimerCommand) {
                is TimerCommand.Start -> startTimer(command.periods)
                is TimerCommand.Pause -> pauseTimer()
                is TimerCommand.Continue -> continueTimer()
                is TimerCommand.Stop -> stopTimer()
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
        serviceState = TimerState.Started
        this.periods = periods
        currentPeriod = firstPeriod
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
        serviceState = TimerState.Paused
        timer.stop()
        broadcastUpdate()
    }

    private fun continueTimer() {
        serviceState = TimerState.Started
        timer.start(
            timeInSeconds = remainingTime.toLong(),
            onTick = this::onTick,
            onComplete = this::updatePeriod
        )
    }

    private fun stopTimer(removeNotification: Boolean = true) {
        serviceState = TimerState.Stopped
        timer.stop()
        broadcastUpdate()
        stopService(removeNotification)
    }

    private fun updatePeriod() {
        serviceState = TimerState.PeriodEnded(currentPeriod!!)
        broadcastUpdate()
        val nextPeriod: Period? = periods?.next()
        if (nextPeriod != null) {
            currentPeriod = nextPeriod
            remainingTime = nextPeriod.time
            timer.start(
                timeInSeconds = remainingTime.toLong(),
                withDelay = POST_PERIOD_DELAY,
                onTick = this::onTick,
                onComplete = this::updatePeriod
            )
        } else {
            stopTimer(false)
        }
    }

    private fun onTick(value: Long) {
        remainingTime--
        broadcastUpdate()
    }

    private fun broadcastUpdate() {
        val string = when (val state = serviceState) {
            is TimerState.Started -> {
                sendBroadcast(Intent(TIMER_ACTION).putExtra(REMAINING_TIME, remainingTime))
                getString(R.string.time_is_running, remainingTime.secondsToTime(this))
            }
            is TimerState.PeriodEnded -> {
                getString(
                    when (state.period) {
                        is Period.Work -> R.string.work_period_ended
                        is Period.Rest -> R.string.rest_period_ended
                        is Period.Preparation -> R.string.preparation_period_ended
                    }
                )
            }
            is TimerState.Paused -> getString(R.string.get_back)
            else -> return
        }
        helper.updateNotification(string)
    }

    private fun stopService(removeNotification: Boolean) {
        stopForeground(removeNotification)
        stopSelf()
    }

    companion object {
        const val SERVICE_COMMAND = "TimerCommand"
        const val TIMER_ACTION = "TimerAction"
        const val REMAINING_TIME = "RemainingTime"
        private const val POST_PERIOD_DELAY = 2500L
    }
}