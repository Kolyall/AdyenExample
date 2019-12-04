package com.github.adyenexample.models

import com.github.kolyall.adyen.model.ApiRecurringDetail

fun ApiRecurringDetail.toViewModel(): CardItem {
    val source = this
    return CardItem().apply {
        brand = source.brand
        expiryMonth = source.expiryMonth
        expiryYear = source.expiryYear
        id = source.id
        holderName = source.name
        isEcommerce = source.isEcommerce()
        supportsRecurring = source.supportsRecurring
        lastFour = source.lastFour
    }
}
