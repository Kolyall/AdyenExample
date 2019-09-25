package com.github.adyenexample.injection

import android.os.Build
import com.adyen.checkout.core.api.SSLSocketUtil
import com.github.adyenexample.BuildConfig
import com.github.adyenexample.api.RxApiServiceCheckout
import com.github.adyenexample.api.error.RxErrorHandlingCallAdapterFactory
import com.google.gson.GsonBuilder
import com.ihsanbal.logging.Level
import com.ihsanbal.logging.LoggingInterceptor
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.internal.platform.Platform
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.KeyStore
import java.util.Arrays
import java.util.concurrent.Executors
import javax.inject.Singleton
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

@Module
class ApiModule {

    @Provides
    @Singleton
    fun providesRxApiServiceCheckout(): RxApiServiceCheckout {
        val loggingInterceptor = LoggingInterceptor.Builder()
            .loggable(BuildConfig.DEBUG)
            .setLevel(Level.BASIC)
            .log(Platform.INFO)
            .request("Request")
            .response("Response")
            .apply {
                if (BuildConfig.DEBUG) {
                    enableAndroidStudio_v3_LogsHack(true)
                        //                    .logger((level, tag, msg) -> {
                        //                        Log.i(tag, msg);
                        //                    })
                        .executor(Executors.newSingleThreadExecutor())
                }
            }
            .build()

        val gson = GsonBuilder()
            .setLenient()
            .create()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.MERCHANT_SERVER_URL)
            .client(Util.enableTls12OnPreLollipop(
                OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
            ))
            .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(RxApiServiceCheckout::class.java)

    }

    class Util private constructor() {

        init {
            throw IllegalStateException("No instances.")
        }

        companion object {
            internal fun enableTls12OnPreLollipop(builder: OkHttpClient.Builder): OkHttpClient {
                if (Build.VERSION_CODES.JELLY_BEAN <= Build.VERSION.SDK_INT
                    && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {

                    val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
                    trustManagerFactory.init(null as KeyStore?)
                    val trustManagers = trustManagerFactory.trustManagers

                    check(!(trustManagers.size != 1 || trustManagers[0] !is X509TrustManager)) { "Unexpected default trust managers:" + Arrays.toString(trustManagers) }

                    val trustManager = trustManagers[0] as X509TrustManager
                    builder.sslSocketFactory(SSLSocketUtil.TLS_SOCKET_FACTORY, trustManager)
                }

                return builder.build()
            }
        }
    }
}