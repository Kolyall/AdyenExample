package com.github.kolyall.adyen

import com.github.kolyall.adyen.model.ApiPaymentMethodsApiResponse
import com.github.kolyall.adyen.model.ApiPaymentMethodsRequest
import com.github.kolyall.adyen.model.ApiPaymentsApiResponse
import com.github.kolyall.adyen.model.ApiPaymentsRequest
import io.reactivex.Maybe
import retrofit2.http.Body
import retrofit2.http.POST

interface AdyenService {

    @POST("payments")
    fun payments(@Body paymentsRequest: ApiPaymentsRequest): Maybe<ApiPaymentsApiResponse>

    @POST("paymentMethods")
    fun paymentMethods(@Body request: ApiPaymentMethodsRequest): Maybe<ApiPaymentMethodsApiResponse>

}
