package com.github.adyenexample.di

import android.app.Application
import com.github.adyenexample.TheApplication
import com.github.kolyall.adyen.ApiModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import dagger.android.support.DaggerApplication
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    ContributesModule::class,
    ModuleApp::class,
    AppModule::class,
    ApiModule::class
])
interface AppComponent : AndroidInjector<DaggerApplication> {
    fun inject(theApplication: TheApplication)

    override fun inject(instance: DaggerApplication)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent

    }

}