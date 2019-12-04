package com.github.kolyall.adyen

import android.os.Build
import com.adyen.checkout.core.api.SSLSocketUtil
import com.github.kolyall.adyen.curl.CurlLoggerInterceptor
import com.github.kolyall.adyen.error.RxErrorHandlingCallAdapterFactory
import com.github.kolyall.java.utils.PlatformLog
import com.google.gson.Gson
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
open class ApiModule {

    @Provides
    @Singleton
    fun providesGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }

    @Provides
    @Singleton
    fun providesRxApiServiceCheckout(gson: Gson, adyenConfig: AdyenConfig, platformLog: PlatformLog): AdyenService {
        val loggingCurlInterceptor = CurlLoggerInterceptor("curl", platformLog)
        val loggingInterceptor = LoggingInterceptor.Builder()
            .loggable(adyenConfig.isDebug)
            .setLevel(Level.BASIC)
            .log(Platform.INFO)
            .request("Request")
            .response("Response")
            .apply {
                if (adyenConfig.isDebug) {
                    enableAndroidStudio_v3_LogsHack(true)
                        //                    .logger((level, tag, msg) -> {
                        //                        Log.i(tag, msg);
                        //                    })
                        .executor(Executors.newSingleThreadExecutor())
                }
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(adyenConfig.merchantServerUrl)
            .client(Util.enableTls12OnPreLollipop(
                OkHttpClient.Builder()
                    .addInterceptor(DefaultAdyenHeaderInterceptor(adyenConfig))
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(loggingCurlInterceptor)
            ))
            .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(AdyenService::class.java)

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