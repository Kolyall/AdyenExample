package com.github.adyenexample

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex


class TheApplication : Application() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

}
