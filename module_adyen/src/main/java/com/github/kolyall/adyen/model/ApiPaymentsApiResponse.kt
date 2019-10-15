/*
 * Copyright (c) 2019 Adyen N.V.
 *
 * This file is open source and available under the MIT license. See the LICENSE file for more info.
 *
 * Created by caiof on 4/6/2019.
 */

package com.github.kolyall.adyen.model

data class ApiPaymentsApiResponse(
    val resultCode: String? = null,
    val paymentData: String? = null,
    val pspReference: String? = null,
    val merchantReference: String? = null,
    val details: List<ApiInputDetail>? = null,
    val action: ApiAction? = null,
    val additionalData: ApiAdditionalData? = null
)
