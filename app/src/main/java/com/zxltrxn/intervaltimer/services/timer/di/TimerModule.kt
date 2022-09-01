package com.zxltrxn.intervaltimer.services.timer.di

import com.zxltrxn.intervaltimer.services.timer.RxTimer
import com.zxltrxn.intervaltimer.services.timer.Timer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent

@Module
@InstallIn(ServiceComponent::class)
abstract class TimerModule {
    @Binds
    abstract fun provideTimer(timer: RxTimer): Timer
}