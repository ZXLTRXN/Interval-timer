package com.zxltrxn.intervaltimer.services.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ServiceContext

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SilentChannel

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SilentNotification

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SoundChannel

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SoundNotification