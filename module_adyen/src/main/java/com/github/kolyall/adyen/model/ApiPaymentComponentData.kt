package com.github.kolyall.adyen.model

class ApiPaymentComponentData<PaymentMethodDetailsT : ApiPaymentMethodDetails> {

    val paymentMethod: PaymentMethodDetailsT? = null
    val storePaymentMethod: Boolean = false
    val shopperReference: String? = null

}
