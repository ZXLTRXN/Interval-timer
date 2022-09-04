package com.zxltrxn.intervaltimer.presentation

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.zxltrxn.intervaltimer.R
import com.zxltrxn.intervaltimer.databinding.PickTimeFragmentBinding
import com.zxltrxn.intervaltimer.services.timer.TimerBroadcastReceiver
import com.zxltrxn.intervaltimer.services.timer.TimerBroadcastReceiverImpl
import com.zxltrxn.intervaltimer.utils.initialize
import com.zxltrxn.intervaltimer.utils.initializeUI
import com.zxltrxn.intervaltimer.utils.observableChangeListener
import com.zxltrxn.intervaltimer.utils.padTo2DigitsString
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class PickTimeFragment : Fragment(R.layout.pick_time_fragment),
    TimerBroadcastReceiver by TimerBroadcastReceiverImpl() {
    private val TAG = this.javaClass.simpleName
    private val binding by viewBinding(PickTimeFragmentBinding::bind)

    private val subscriptions = CompositeDisposable()
    private var currentTime: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeTimePicker()
        initializeListeners()
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

    private fun initializeListeners() {
        with(binding) {
            val observablesList: List<Observable<Int>> = listOf(
                timePicker.numPickerHours.observableChangeListener(),
                timePicker.numPickerMinutes.observableChangeListener(),
                timePicker.numPickerSeconds.observableChangeListener()
            )
            Observable.combineLatest(observablesList) { it.toList() as List<Int> }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .debounce(150L, TimeUnit.MILLISECONDS)
                .subscribe { time ->
                    currentTime = time[0] * SECONDS_IN_HOUR + time[1] * SECONDS_IN_MINUTE + time[2]
                    Log.d(
                        TAG,
                        "initializeListeners: ${time[0]} ${time[1]} ${time[2]} current $currentTime"
                    )
                }

//            RxView.clicks(startButton).subscribe {
//            }
        }
    }

    private companion object {
        const val SECONDS_IN_HOUR = 3600
        const val SECONDS_IN_MINUTE = 60
        const val MAX_HOURS = 99
        const val MAX_MINUTES_SECONDS = 59
        const val NUM_PICKER_TEXT_SIZE: Float = 125F
    }
}