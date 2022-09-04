package com.zxltrxn.intervaltimer.presentation;

import android.util.Log;

import androidx.lifecycle.ViewModel;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
class PickTimeViewModel extends ViewModel {
    private final TimePicker timePicker;

    @Inject
    public PickTimeViewModel(TimePicker timePicker) {
        this.timePicker = timePicker;
    }

    public TimePicker getTimePicker() {
        return timePicker;
    }

    public void onStartTimerPressed() {
        Log.d("AAA", String.format("onStartTimerPressed: %d", timePicker.getTimeInSeconds()));
    }
}
