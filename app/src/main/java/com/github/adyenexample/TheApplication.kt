package com.github.adyenexample

import android.content.Context
import androidx.multidex.MultiDex
import com.github.adyenexample.di.AppComponent
import com.github.adyenexample.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication


class TheApplication : DaggerApplication() {

    lateinit var component: AppComponent

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        component = DaggerAppComponent.builder().application(this).build()
        component.inject(this)
        return component
    }

    companion object {
        val TAG: String = TheApplication::class.java.simpleName

        fun getAppComponent(context: Context): AppComponent {
            return get(context).component
        }

        fun get(context: Context): TheApplication {
            return context.applicationContext as TheApplication
        }
    }

}
