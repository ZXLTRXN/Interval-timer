package com.zxltrxn.intervaltimer.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.zxltrxn.intervaltimer.R
import com.zxltrxn.intervaltimer.databinding.TimerFragmentBinding
import by.kirich1409.viewbindingdelegate.viewBinding
import com.zxltrxn.intervaltimer.services.timer.TimerServiceCommander
import com.zxltrxn.intervaltimer.services.timer.TimerServiceCommanderImpl
import com.zxltrxn.intervaltimer.services.timer.TimerState

class TimerFragment : Fragment(R.layout.timer_fragment),
    TimerServiceCommander by TimerServiceCommanderImpl() {
    private val binding by viewBinding(TimerFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.remainingTime.text = "12:12"
        binding.buttonOn.setOnClickListener {
            sendCommandToService(requireActivity(), TimerState.START)
        }
        binding.buttonOff.setOnClickListener {
            sendCommandToService(requireActivity(), TimerState.STOP)
        }
    }
}