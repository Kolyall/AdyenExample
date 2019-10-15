package com.github.kolyall.adyen

data class AdyenConfig(
    val isDebug: Boolean,
    val MERCHANT_SERVER_URL: String,
    val API_KEY_HEADER_NAME: String,
    val CHECKOUT_API_KEY: String
) {

}
