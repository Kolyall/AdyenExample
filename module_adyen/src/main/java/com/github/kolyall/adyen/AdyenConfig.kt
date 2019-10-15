package com.github.kolyall.adyen

import com.github.kolyall.adyen.model.ApiAmount

data class AdyenConfig(
    val isDebug: Boolean,
    val merchantServerUrl: String,
    val apiKeyHeaderName: String,
    val checkoutApiKey: String,
    val merchantAccount: String,
    val returnUrl: String,
    val defaultAmount: ApiAmount
)
