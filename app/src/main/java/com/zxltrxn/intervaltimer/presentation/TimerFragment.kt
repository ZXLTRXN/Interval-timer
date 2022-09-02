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
import com.zxltrxn.intervaltimer.utils.observableChangeListener
import com.zxltrxn.intervaltimer.utils.padTo2DigitsString
import io.reactivex.rxjava3.disposables.CompositeDisposable

class TimerFragment : Fragment(R.layout.timer_fragment),
    TimerBroadcastReceiver by TimerBroadcastReceiverImpl() {
    private val TAG = this.javaClass.simpleName
    private val binding by viewBinding(TimerFragmentBinding::bind)

    private var events: TimerFragmentEvents? = null
    private val subscriptions = CompositeDisposable()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeTimePicker()
        bindTimePicker()
//        bindReceiver(this, requireContext()) { time ->
//            binding.remainingTime.text = time.secondsToTime(requireContext())
//        }
    }

    override fun onPause() {
        super.onPause()
        subscriptions.dispose()
    }

    private fun initializeTimePicker() {
        val necessaryDigits: List<String> =
            (0..maxOf(MAX_HOURS, MAX_MINUTES_SECONDS)).map { it.padTo2DigitsString() }
        with(binding.timePicker) {
            numPickerHours.run {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) initializeUI(
                    NUM_PICKER_TEXT_SIZE
                )
                initialize(displayedValues = necessaryDigits, maxValue = MAX_HOURS)
            }
            numPickerMinutes.run {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) initializeUI(
                    NUM_PICKER_TEXT_SIZE
                )
                initialize(displayedValues = necessaryDigits, maxValue = MAX_MINUTES_SECONDS)
            }
            numPickerSeconds.run {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) initializeUI(
                    NUM_PICKER_TEXT_SIZE
                )
                initialize(displayedValues = necessaryDigits, maxValue = MAX_MINUTES_SECONDS)
            }
        }
    }

    private fun bindTimePicker() {
        with(binding.timePicker) {
            events = TimerFragmentEvents(
                numPickerHours.observableChangeListener(),
                numPickerMinutes.observableChangeListener(),
                numPickerSeconds.observableChangeListener()
            )
        }
    }

    private companion object {
        const val MAX_HOURS = 99
        const val MAX_MINUTES_SECONDS = 59
        const val NUM_PICKER_TEXT_SIZE: Float = 125F
    }
}