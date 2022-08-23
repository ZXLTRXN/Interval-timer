package com.zxltrxn.intervaltimer.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.zxltrxn.intervaltimer.R
import com.zxltrxn.intervaltimer.databinding.TimerFragmentBinding
import by.kirich1409.viewbindingdelegate.viewBinding

class TimerFragment : Fragment(R.layout.timer_fragment) {
    private val binding by viewBinding(TimerFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.remainingTime.text = "12:12"
    }
}