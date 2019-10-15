package com.github.kolyall.adyen.model

class ApiCardPaymentMethod : ApiPaymentMethodDetails() {

    var encryptedCardNumber: String? = null
    var encryptedExpiryMonth: String? = null
    var encryptedExpiryYear: String? = null
    var encryptedSecurityCode: String? = null
    var holderName: String? = null
    var recurringDetailReference: String? = null

}
