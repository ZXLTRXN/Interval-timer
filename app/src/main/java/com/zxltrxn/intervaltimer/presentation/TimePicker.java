package com.zxltrxn.intervaltimer.presentation;

import android.widget.NumberPicker;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import com.zxltrxn.intervaltimer.utils.NumberPickerExtensionsKt;

import java.util.Objects;

import javax.inject.Inject;

class TimePicker {
    private final MutableLiveData<Integer> hoursLiveData;
    private final MutableLiveData<Integer> minutesLiveData;
    private final MutableLiveData<Integer> secondsLiveData;

    @Inject
    public TimePicker() {
        int defaultValue = 0;
        this.hoursLiveData = new MutableLiveData<>(defaultValue);
        this.minutesLiveData = new MutableLiveData<>(defaultValue);
        this.secondsLiveData = new MutableLiveData<>(defaultValue);
    }

    public void bindTimePickerTwoWay(
            @NonNull NumberPicker hours,
            @NonNull NumberPicker minutes,
            @NonNull NumberPicker seconds,
            @NonNull LifecycleOwner lifecycleOwner
    ) {
        NumberPickerExtensionsKt.bindTwoWay(hours, hoursLiveData, lifecycleOwner);
        NumberPickerExtensionsKt.bindTwoWay(minutes, minutesLiveData, lifecycleOwner);
        NumberPickerExtensionsKt.bindTwoWay(seconds, secondsLiveData, lifecycleOwner);
    }

    public int getTimeInSeconds() {
        try {

            Integer hours = Objects.requireNonNull(hoursLiveData.getValue());
            int minutes = Objects.requireNonNull(minutesLiveData.getValue());
            int seconds = Objects.requireNonNull(secondsLiveData.getValue());
            return  hours * SECONDS_IN_HOUR + minutes * SECONDS_IN_MINUTE + seconds;
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("LiveData must have initial value", e);
        }
    }

    private static final int SECONDS_IN_HOUR = 3600;
    private static final int SECONDS_IN_MINUTE = 60;
}
