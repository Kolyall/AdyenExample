package com.github.adyenexample.models

class CardItem {
    var isEcommerce: Boolean = false
    var supportsRecurring: Boolean = false
    var id: String? = null
    var holderName: String? = null
    lateinit var lastFour: String
    lateinit var expiryYear: String
    lateinit var expiryMonth: String
    lateinit var brand: String
}