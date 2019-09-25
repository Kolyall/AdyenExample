package com.github.adyenexample.injection

import com.github.adyenexample.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ContributesModule {
    @ContributesAndroidInjector(modules = [])
    internal abstract fun mainActivity(): MainActivity
}