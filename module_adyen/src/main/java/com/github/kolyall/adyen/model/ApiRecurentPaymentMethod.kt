package com.github.kolyall.adyen.model

class ApiRecurentPaymentMethod : ApiPaymentMethodDetails() {
    var recurringDetailReference: String? = null
    var storedPaymentMethodId: String? = null
    var encryptedCardNumber: String? = null
    var encryptedExpiryMonth:String? = null
    var encryptedExpiryYear:String? = null
    var encryptedSecurityCode:String? = null
    var holderName:String? = null
}