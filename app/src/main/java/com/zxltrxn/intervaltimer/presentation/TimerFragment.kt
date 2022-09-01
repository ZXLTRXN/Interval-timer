package com.zxltrxn.intervaltimer.presentation

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.zxltrxn.intervaltimer.R
import com.zxltrxn.intervaltimer.databinding.TimerFragmentBinding
import com.zxltrxn.intervaltimer.services.timer.TimerBroadcastReceiver
import com.zxltrxn.intervaltimer.services.timer.TimerBroadcastReceiverImpl
import com.zxltrxn.intervaltimer.utils.initialize
import com.zxltrxn.intervaltimer.utils.initializeUI
import com.zxltrxn.intervaltimer.utils.padTo2DigitsString

class TimerFragment : Fragment(R.layout.timer_fragment),
    TimerBroadcastReceiver by TimerBroadcastReceiverImpl() {
    private val TAG = this.javaClass.simpleName
    private val binding by viewBinding(TimerFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeTimePicker()
//        bindReceiver(this, requireContext()) { time ->
//            binding.remainingTime.text = time.secondsToTime(requireContext())
//        }
    }

    private fun initializeTimePicker() {
        val hoursMax = 99
        val minutesSecondsMax = 59
        val necessaryDigits: List<String> =
            (0..maxOf(hoursMax, minutesSecondsMax)).map { it.padTo2DigitsString() }
        val textSize = 120F
        with(binding.timePicker) {
            numPickerHours.run {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) initializeUI(textSize)
                initialize(displayedValues = necessaryDigits, maxValue = hoursMax)
                setOnValueChangedListener { _, _, newValue ->
                }
            }
            numPickerMinutes.run {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) initializeUI(textSize)
                initialize(displayedValues = necessaryDigits, maxValue = minutesSecondsMax)
                setOnValueChangedListener { _, _, newValue ->
                }
            }
            numPickerSeconds.run {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) initializeUI(textSize)
                initialize(displayedValues = necessaryDigits, maxValue = minutesSecondsMax)
                setOnValueChangedListener { _, _, newValue ->
                }
            }
        }
    }
}