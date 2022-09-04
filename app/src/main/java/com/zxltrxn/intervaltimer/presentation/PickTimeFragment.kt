package com.zxltrxn.intervaltimer.presentation

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.jakewharton.rxbinding.view.RxView
import com.zxltrxn.intervaltimer.R
import com.zxltrxn.intervaltimer.databinding.PickTimeFragmentBinding
import com.zxltrxn.intervaltimer.services.timer.TimerBroadcastReceiver
import com.zxltrxn.intervaltimer.services.timer.TimerBroadcastReceiverImpl
import com.zxltrxn.intervaltimer.utils.initialize
import com.zxltrxn.intervaltimer.utils.initializeUI
import com.zxltrxn.intervaltimer.utils.padTo2DigitsString
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PickTimeFragment : Fragment(R.layout.pick_time_fragment),
    TimerBroadcastReceiver by TimerBroadcastReceiverImpl() {
    private val TAG = this.javaClass.simpleName
    private val binding by viewBinding(PickTimeFragmentBinding::bind)
    private val viewModel: PickTimeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeTimePicker()
        initializeListeners()

//        bindReceiver(this, requireContext()) { time ->
//            binding.remainingTime.text = time.secondsToTime(requireContext())
//        }
    }

    private fun initializeTimePicker() {
        val necessaryDigits: List<String> =
            (0..maxOf(MAX_HOURS, MAX_MINUTES_SECONDS)).map { it.padTo2DigitsString() }
        with(binding.timePicker) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                numPickerHours.initializeUI(NUM_PICKER_TEXT_SIZE)
                numPickerMinutes.initializeUI(NUM_PICKER_TEXT_SIZE)
                numPickerSeconds.initializeUI(NUM_PICKER_TEXT_SIZE)
            }

            numPickerHours.initialize(displayedValues = necessaryDigits, maxValue = MAX_HOURS)
            numPickerMinutes.initialize(
                displayedValues = necessaryDigits, maxValue = MAX_MINUTES_SECONDS
            )
            numPickerSeconds.initialize(
                displayedValues = necessaryDigits,
                maxValue = MAX_MINUTES_SECONDS
            )

            viewModel.timePicker.bindTimePickerTwoWay(
                numPickerHours,
                numPickerMinutes,
                numPickerSeconds,
                viewLifecycleOwner
            )
        }
    }

    private fun initializeListeners() {
        RxView.clicks(binding.startButton).subscribe {
            viewModel.onStartTimerPressed()
        }
    }

    private companion object {
        const val MAX_HOURS = 99
        const val MAX_MINUTES_SECONDS = 59
        const val NUM_PICKER_TEXT_SIZE: Float = 125F
    }
}