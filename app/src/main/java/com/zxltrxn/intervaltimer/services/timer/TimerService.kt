package com.zxltrxn.intervaltimer.services.timer

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.zxltrxn.intervaltimer.R
import com.zxltrxn.intervaltimer.services.timer.model.Period
import com.zxltrxn.intervaltimer.services.timer.model.TimePeriods
import com.zxltrxn.intervaltimer.services.timer.model.TimerCommand
import com.zxltrxn.intervaltimer.services.timer.model.TimerState
import com.zxltrxn.intervaltimer.utils.secondsToTime
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TimerService : Service(), CoroutineScope {
    override val coroutineContext: CoroutineContext get() = Dispatchers.IO + job

    @Inject
    lateinit var job: Job

    @Inject
    lateinit var helper: NotificationHelper

    private var serviceState: TimerState = TimerState.Initialized

    private var remainingTime: Int = -1
        set(value) {
            field = value
            if (value == 0) updatePeriod()
        }
    private var periods: TimePeriods? = null
    private lateinit var currentPeriod: Period

    private val handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable = object : Runnable {
        override fun run() {
            remainingTime--
            broadcastUpdate()
            handler.postDelayed(this, DELAY)
        }
    }

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
        handler.removeCallbacks(runnable)
        job.cancel()
    }

    private fun startTimer(periods: TimePeriods) {
        val firstPeriod: Period = periods.next() ?: return
        serviceState = TimerState.Started
        this.periods = periods
        currentPeriod = firstPeriod
        remainingTime = currentPeriod.time
        startForeground(NotificationHelper.NOTIFICATION_ID, helper.getNotification())
        broadcastUpdate()
        startCoroutineTimer()
    }

    private fun updatePeriod() {
        handler.removeCallbacks(runnable)
        serviceState = TimerState.PeriodEnded(currentPeriod)
        broadcastUpdate()
        val nextPeriod: Period? = periods?.next()
        if (nextPeriod != null) {
            currentPeriod = nextPeriod
            remainingTime = nextPeriod.time
            continueTimer()
        } else {
            stopTimer(false)
        }
    }

    private fun pauseTimer() {
        serviceState = TimerState.Paused
        handler.removeCallbacks(runnable)
        broadcastUpdate()
    }

    private fun continueTimer() {
        serviceState = TimerState.Started
        broadcastUpdate()
        startCoroutineTimer()
    }

    private fun stopTimer(removeNotification: Boolean = true) {
        serviceState = TimerState.Stopped
        handler.removeCallbacks(runnable)
        job.cancel()
        broadcastUpdate()
        stopService(removeNotification)
    }

    private fun broadcastUpdate() {
        val string = when (val state = serviceState) {
            is TimerState.Started -> {
                sendBroadcast(Intent(TIMER_ACTION).putExtra(REMAINING_TIME, remainingTime))
                getString(R.string.time_is_running, remainingTime.secondsToTime(this))
            }
            is TimerState.PeriodEnded -> {
                val stringRes: Int = when (state.period) {
                    is Period.Work -> R.string.work_period_ended
                    is Period.Rest -> R.string.rest_period_ended
                    is Period.Preparation -> R.string.preparation_period_ended
                }
                getString(stringRes)
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

    private fun startCoroutineTimer() {
        launch(coroutineContext) {
            handler.post(runnable)
        }
    }

    companion object {
        const val SERVICE_COMMAND = "TimerCommand"
        const val TIMER_ACTION = "TimerAction"
        const val REMAINING_TIME = "RemainingTime"
        private const val DELAY: Long = 1000
        private const val POST_PERIOD_DELAY: Long = 3000
    }
}