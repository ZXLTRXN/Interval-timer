package com.zxltrxn.intervaltimer.presentation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.zxltrxn.intervaltimer.R
import com.zxltrxn.intervaltimer.databinding.TimerFragmentBinding
import com.zxltrxn.intervaltimer.services.timer.TimerService.Companion.REMAINING_TIME
import com.zxltrxn.intervaltimer.services.timer.TimerService.Companion.TIMER_ACTION
import com.zxltrxn.intervaltimer.services.timer.TimerServiceCommander
import com.zxltrxn.intervaltimer.services.timer.TimerServiceCommanderImpl
import com.zxltrxn.intervaltimer.services.timer.model.TimePeriods
import com.zxltrxn.intervaltimer.services.timer.model.TimerCommand
import com.zxltrxn.intervaltimer.utils.secondsToTime

class TimerFragment : Fragment(R.layout.timer_fragment),
    TimerServiceCommander by TimerServiceCommanderImpl() {
    private val binding by viewBinding(TimerFragmentBinding::bind)
    private val timerReceiver: BroadcastReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == TIMER_ACTION) {
                    binding.remainingTime.text = intent
                        .getIntExtra(REMAINING_TIME, 0)
                        .secondsToTime(requireContext())
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind()
    }

    override fun onResume() {
        super.onResume()
//        if (!mainViewModel.isReceiverRegistered) {
        requireContext().registerReceiver(timerReceiver, IntentFilter(TIMER_ACTION))
//            mainViewModel.isReceiverRegistered = true
//        }
    }

    override fun onPause() {
        super.onPause()
//        if (mainViewModel.isReceiverRegistered) {
        requireContext().unregisterReceiver(timerReceiver)
//            mainViewModel.isReceiverRegistered = false
//        }
    }

    private fun bind() {
        val def: Int = 0
        val periods = TimePeriods(2, 2, 2, 1)
        binding.remainingTime.text = def.secondsToTime(requireContext())
        binding.buttonOn.setOnClickListener {
            sendCommandToService(requireActivity(), TimerCommand.Start(periods))
        }
        binding.buttonOff.setOnClickListener {
            sendCommandToService(requireActivity(), TimerCommand.Stop)
        }
    }
}