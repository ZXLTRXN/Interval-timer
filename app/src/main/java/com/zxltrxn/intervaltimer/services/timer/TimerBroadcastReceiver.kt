package com.zxltrxn.intervaltimer.services.timer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

interface TimerBroadcastReceiver {
    fun bindReceiver(owner: LifecycleOwner, context: Context, onReceive: (Int) -> Unit)
}

class TimerBroadcastReceiverImpl : TimerBroadcastReceiver,
    LifecycleEventObserver {
    private lateinit var onReceive: (Int) -> Unit
    private var context: Context? = null

    private val timerReceiver: BroadcastReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == TimerService.TIMER_ACTION) onReceive(
                    intent.getIntExtra(TimerService.REMAINING_TIME, 0)
                )
            }
        }
    }

    override fun bindReceiver(owner: LifecycleOwner, context: Context, onReceive: (Int) -> Unit) {
        owner.lifecycle.addObserver(this)
        this.onReceive = onReceive
        this.context = context
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
//        if (!mainViewModel.isReceiverRegistered) {}
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                context?.registerReceiver(
                    timerReceiver,
                    IntentFilter(TimerService.TIMER_ACTION)
                )
            }
            Lifecycle.Event.ON_PAUSE -> {
                context?.unregisterReceiver(timerReceiver)
            }
            else -> Unit
        }
    }
}