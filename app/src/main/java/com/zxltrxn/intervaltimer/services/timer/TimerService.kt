package com.zxltrxn.intervaltimer.services.timer

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.zxltrxn.intervaltimer.R
import com.zxltrxn.intervaltimer.utils.secondsToTime
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class TimerService : Service(), CoroutineScope {
    override val coroutineContext: CoroutineContext get() = Dispatchers.IO + job
    private val job = Job()

    private var serviceState: TimerState = TimerState.INITIALIZED

    private val helper by lazy { NotificationHelper(this) }

    private var currentTime: Int = 0
    private var startedAtTimestamp: Int = 0
        set(value) {
            currentTime = value
            field = value
        }

    private val handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable = object : Runnable {
        override fun run() {
            currentTime++
            broadcastUpdate()
            handler.postDelayed(this, DELAY)
        }
    }

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        intent?.extras?.run {
            when (getSerializable(SERVICE_COMMAND) as TimerState) {
                TimerState.START -> startTimer()
                TimerState.PAUSE -> pauseTimer()
                TimerState.STOP -> stopTimer()
                else -> return START_NOT_STICKY
            }
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
        job.cancel()
    }

    private fun startTimer(elapsedTime: Int? = null) {
        serviceState = TimerState.START
        startedAtTimestamp = elapsedTime ?: 0
        startForeground(NotificationHelper.NOTIFICATION_ID, helper.getNotification())
        broadcastUpdate()
        startCoroutineTimer()
    }

    private fun pauseTimer() {
        serviceState = TimerState.PAUSE
        handler.removeCallbacks(runnable)
        broadcastUpdate()
    }

    private fun stopTimer() {
        serviceState = TimerState.STOP
        handler.removeCallbacks(runnable)
        job.cancel()
        broadcastUpdate()
        stopService()
    }

    private fun broadcastUpdate() {
        if (serviceState == TimerState.START) {
            val elapsedTime: Int = (currentTime - startedAtTimestamp)
            sendBroadcast(
                Intent(TIMER_ACTION)
                    .putExtra(REMAINING_TIME, elapsedTime)
            )
            helper.updateNotification(
                getString(
                    R.string.time_is_running,
                    elapsedTime.secondsToTime(this)
                )
            )
        } else if (serviceState == TimerState.PAUSE) {
            helper.updateNotification(getString(R.string.get_back))
        }
    }

    private fun stopService() {
        stopForeground(true)
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
    }
}