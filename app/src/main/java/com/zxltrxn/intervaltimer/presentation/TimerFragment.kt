package com.zxltrxn.intervaltimer.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.zxltrxn.intervaltimer.R
import com.zxltrxn.intervaltimer.databinding.TimerFragmentBinding
import com.zxltrxn.intervaltimer.services.timer.TimerBroadcastReceiver
import com.zxltrxn.intervaltimer.services.timer.TimerBroadcastReceiverImpl
import com.zxltrxn.intervaltimer.services.timer.model.TimePeriods
import com.zxltrxn.intervaltimer.services.timer.model.TimerCommand
import com.zxltrxn.intervaltimer.utils.secondsToTime
import com.zxltrxn.intervaltimer.utils.sendCommandToTimer

class TimerFragment : Fragment(R.layout.timer_fragment),
    TimerBroadcastReceiver by TimerBroadcastReceiverImpl() {
    private val binding by viewBinding(TimerFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindReceiver(this, requireContext()) { time ->
            binding.remainingTime.text = time.secondsToTime(requireContext())
        }
        bind()
    }

    private fun bind() {
        val periods = TimePeriods(0, 2, 2, 2)
        binding.remainingTime.text = 0.secondsToTime(requireContext())
        binding.buttonOn.setOnClickListener {
            sendCommandToTimer(TimerCommand.Start(periods))
        }
        binding.buttonOff.setOnClickListener {
            sendCommandToTimer(TimerCommand.Stop)
        }
    }
}