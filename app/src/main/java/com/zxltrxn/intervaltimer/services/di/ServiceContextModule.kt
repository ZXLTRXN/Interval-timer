package com.zxltrxn.intervaltimer.services.di

import android.app.Service
import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent

@Module
@InstallIn(ServiceComponent::class)
abstract class ServiceContextModule {
    @Binds
    @ServiceContext
    abstract fun provideContext(service: Service): Context
}
