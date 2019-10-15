package com.github.kolyall.adyen.model

import com.google.gson.annotations.SerializedName

data class ApiAdditionalData(
    val allow3DS2: Boolean = false,
    val recurringProcessingModel: String? = null,
    @SerializedName("recurring.recurringDetailReference")
    val recurringDetailReference: String? = null,
    @SerializedName("recurring.shopperReference")
    val shopperReference: String? = null
)
