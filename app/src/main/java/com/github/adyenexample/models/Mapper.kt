package com.github.adyenexample.models

import com.adyen.checkout.base.model.paymentmethods.PaymentMethod
import com.adyen.checkout.base.model.payments.request.CardPaymentMethod
import com.adyen.checkout.base.model.payments.request.PaymentMethodDetails
import com.github.kolyall.adyen.model.ApiCardPaymentMethod
import com.github.kolyall.adyen.model.ApiGenericPaymentMethod
import com.github.kolyall.adyen.model.ApiPaymentMethod
import com.github.kolyall.adyen.model.ApiPaymentMethodDetails
import com.github.kolyall.adyen.model.ApiRecurringDetail
import com.google.gson.Gson
import org.json.JSONObject

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

fun ApiPaymentMethod.toAdyen(gson: Gson): PaymentMethod {
    return PaymentMethod.SERIALIZER.deserialize(JSONObject(gson.toJson(this)))
}

fun PaymentMethodDetails.toApi(gson: Gson): ApiPaymentMethodDetails {
    val jsonObject = PaymentMethodDetails.SERIALIZER.serialize(this).toString()
    when (this.type) {
//        IdealPaymentMethod.PAYMENT_METHOD_TYPE -> return IdealPaymentMethod.SERIALIZER
        CardPaymentMethod.PAYMENT_METHOD_TYPE -> return gson.fromJson(jsonObject, ApiCardPaymentMethod::class.java)
//        PaymentMethodTypes.MOLPAY_MALAYSIA, PaymentMethodTypes.MOLPAY_THAILAND, PaymentMethodTypes.MOLPAY_VIETNAM -> return MolpayPaymentMethod.SERIALIZER
//        DotpayPaymentMethod.PAYMENT_METHOD_TYPE -> return DotpayPaymentMethod.SERIALIZER
//        EPSPaymentMethod.PAYMENT_METHOD_TYPE -> return EPSPaymentMethod.SERIALIZER
//        OpenBankingPaymentMethod.PAYMENT_METHOD_TYPE -> return OpenBankingPaymentMethod.SERIALIZER
//        EntercashPaymentMethod.PAYMENT_METHOD_TYPE -> return EntercashPaymentMethod.SERIALIZER
//        GooglePayPaymentMethod.PAYMENT_METHOD_TYPE -> return GooglePayPaymentMethod.SERIALIZER
//        SepaPaymentMethod.PAYMENT_METHOD_TYPE -> return SepaPaymentMethod.SERIALIZER
        else -> return gson.fromJson(jsonObject, ApiGenericPaymentMethod::class.java)
    }
}
