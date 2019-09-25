/*
 * Copyright (c) 2019 Adyen N.V.
 *
 * This file is open source and available under the MIT license. See the LICENSE file for more info.
 *
 * Created by caiof on 11/2/2019.
 */

package com.adyen.checkout.example.api

import android.os.Build
import com.adyen.checkout.core.api.SSLSocketUtil
import com.github.adyenexample.BuildConfig
import com.github.adyenexample.api.model.PaymentsApiResponse
import com.google.gson.GsonBuilder
import com.ihsanbal.logging.Level
import com.ihsanbal.logging.LoggingInterceptor
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.internal.platform.Platform
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import java.security.KeyStore
import java.util.Arrays
import java.util.concurrent.Executors
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

interface CheckoutApiService {

    companion object {
        val INSTANCE: CheckoutApiService by lazy {


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

            Retrofit.Builder()
                .baseUrl(BuildConfig.MERCHANT_SERVER_URL)
                .client(Util.enableTls12OnPreLollipop(
                    OkHttpClient.Builder()
                        .addInterceptor(loggingInterceptor)
                ))
//                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(MoshiConverterFactory.create(moshi))
//                    .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()
                .create(CheckoutApiService::class.java)
        }
    }

//    @Headers(BuildConfig.API_KEY_HEADER_NAME + ":" + BuildConfig.CHECKOUT_API_KEY)
//    @POST("paymentMethods")
//    fun paymentMethodsAsync(@Body paymentMethodsRequest: PaymentMethodsRequest): Deferred<Response<PaymentMethodsApiResponse>>

    // There is no native support for JSONObject in either Moshi or Gson, so using RequestBody as a work around for now
    @Headers(BuildConfig.API_KEY_HEADER_NAME + ":" + BuildConfig.CHECKOUT_API_KEY)
    @POST("payments")
    fun payments(@Body paymentsRequest: RequestBody): Call<PaymentsApiResponse>

    @Headers(BuildConfig.API_KEY_HEADER_NAME + ":" + BuildConfig.CHECKOUT_API_KEY)
    @POST("payments/details")
    fun details(@Body detailsRequest: RequestBody): Call<PaymentsApiResponse>

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
