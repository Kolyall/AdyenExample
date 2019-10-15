package com.github.kolyall.adyen

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class DefaultAdyenHeaderInterceptor(val adyenConfig: AdyenConfig):Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val builder = request.newBuilder()
        builder
            .header("Accept", "application/json")
            .header("content-type", "application/json")
            .header(adyenConfig.API_KEY_HEADER_NAME, adyenConfig.CHECKOUT_API_KEY)

        return chain.proceed(builder.build())
    }

}
