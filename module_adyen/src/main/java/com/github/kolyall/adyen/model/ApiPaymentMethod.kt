package com.github.kolyall.adyen.model

import java.security.acl.Group

open class ApiPaymentMethod {

    val configuration: String? = null
    val details: List<ApiInputDetail>? = null
    val group: Group? = null
    val name: String? = null
    val brands: List<String>? = null
    val paymentMethodData: String? = null
    val supportsRecurring: Boolean = false
    val type: String? = null

}
