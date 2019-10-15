package com.github.adyenexample.injection

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import com.adyen.checkout.redirect.RedirectComponent
import com.github.adyenexample.BuildConfig
import com.github.adyenexample.ext.SchedulersProvider
import com.github.kolyall.adyen.AdyenConfig
import com.github.kolyall.adyen.model.ApiAmount
import com.github.kolyall.java.utils.PlatformLog
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@Module
class AppModule {

    @Provides
    fun providePlatformLog(): PlatformLog {
        return object : PlatformLog {
            override fun d(tag: String, msg: String) {
                Log.d(tag, msg)
            }

            override fun e(tag: String, msg: String, throwable: Throwable?) {
                Log.e(tag, msg, throwable)
            }

            override fun i(tag: String, msg: String) {
                Log.i(tag, msg)
            }
        }
    }

    @Provides
    fun provideAdyenConfig(context: Context): AdyenConfig {
        val defaultValue = 0
        val defaultCurrency = "EUR"
        return AdyenConfig(
            BuildConfig.DEBUG,
            BuildConfig.MERCHANT_SERVER_URL,
            BuildConfig.API_KEY_HEADER_NAME,
            BuildConfig.CHECKOUT_API_KEY,
            BuildConfig.MERCHANT_ACCOUNT,
            RedirectComponent.getReturnUrl(context),
            ApiAmount().apply {
                currency = defaultCurrency
                value = defaultValue
            }
        )
    }

    @Provides
    fun provideSharedPreferences(context: Application): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    @Provides
    fun provideSchedulersProvider(): SchedulersProvider {
        return object : SchedulersProvider {
            override fun computation(): Scheduler {
                return Schedulers.computation()
            }

            override fun io(): Scheduler {
                return Schedulers.io()
            }

            override fun ui(): Scheduler {
                return AndroidSchedulers.mainThread()
            }
        }
    }

}
