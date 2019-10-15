package com.github.kolyall.adyen.model

data class ApiPaymentMethodsRequest(
    val merchantAccount: String,
    val shopperReference: String,
//    val additionalData: Any,
//    val allowedPaymentMethods: ArrayList<String>,
    val amount: ApiAmount,
//    val blockedPaymentMethods: ArrayList<String>,
    val countryCode: String = "NL",
    val shopperLocale: String = "en_US",
    val channel: String = "android"
)
