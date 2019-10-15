
package com.github.kolyall.adyen.model


data class ApiPaymentsRequest(
    val paymentMethod: ApiPaymentMethodDetails,
    val shopperReference: String,
    val recurringProcessingModel: String?=null,
    val shopperInteraction: String?=null,
    val storePaymentMethod: Boolean,
    val amount: ApiAmount,
    val merchantAccount: String,
    // unique reference of the payment
    val returnUrl: String,
    val reference: String = "android-test-components",
    val channel: String = "android",
    val additionalData: ApiAdditionalData = ApiAdditionalData()
)
