package com.github.adyenexample.di

import android.app.Application
import android.content.Context
import dagger.Binds
import dagger.Module

@Module
abstract class ModuleApp {

    @Binds
    abstract fun provideContext(application: Application): Context

}
