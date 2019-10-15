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
            .header(adyenConfig.apiKeyHeaderName, adyenConfig.checkoutApiKey)

        return chain.proceed(builder.build())
    }

}
