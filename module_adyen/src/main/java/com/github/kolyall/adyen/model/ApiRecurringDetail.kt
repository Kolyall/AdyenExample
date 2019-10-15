package com.github.kolyall.adyen.model

class ApiRecurringDetail : ApiPaymentMethod() {

    private val ECOMMERCE = "Ecommerce"

    val id: String? = null
    lateinit var expiryMonth: String
    lateinit var expiryYear: String
    lateinit var lastFour: String
    lateinit var brand: String
    val supportedShopperInteractions = emptyList<String>()

    fun isEcommerce(): Boolean {
        return supportedShopperInteractions.contains(ECOMMERCE)
    }

}
