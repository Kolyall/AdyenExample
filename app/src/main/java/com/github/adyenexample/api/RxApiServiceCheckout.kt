/*
 * Copyright (c) 2019 Adyen N.V.
 *
 * This file is open source and available under the MIT license. See the LICENSE file for more info.
 *
 * Created by caiof on 11/2/2019.
 */

package com.github.adyenexample.api

import com.adyen.checkout.base.model.PaymentMethodsApiResponse
import com.github.adyenexample.BuildConfig
import com.github.adyenexample.api.model.PaymentMethodsRequest
import com.github.adyenexample.api.model.PaymentsApiResponse
import com.github.adyenexample.api.model.PaymentsRequest
import io.reactivex.Maybe
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface RxApiServiceCheckout {

    @Headers(
        BuildConfig.API_KEY_HEADER_NAME + ":" + BuildConfig.CHECKOUT_API_KEY,
        "content-type:application/json"
    )
    @POST("payments")
    fun payments(@Body paymentsRequest: PaymentsRequest): Maybe<PaymentsApiResponse>

    @Headers(
        BuildConfig.API_KEY_HEADER_NAME + ":" + BuildConfig.CHECKOUT_API_KEY,
        "content-type:application/json"
    )
    @POST("payments/details")
    fun details(@Body detailsRequest: RequestBody): Maybe<PaymentsApiResponse>

    @Headers(
        BuildConfig.API_KEY_HEADER_NAME + ":" + BuildConfig.CHECKOUT_API_KEY,
        "content-type:application/json"
    )
    @POST("paymentMethods")
    fun paymentMethods(@Body request: PaymentMethodsRequest): Maybe<PaymentMethodsApiResponse>

}
