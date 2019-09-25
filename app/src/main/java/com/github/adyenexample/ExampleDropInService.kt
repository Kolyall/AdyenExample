/*
 * Copyright (c) 2019 Adyen N.V.
 *
 * This file is open source and available under the MIT license. See the LICENSE file for more info.
 *
 * Created by caiof on 22/3/2019.
 */

package com.github.adyenexample

import android.util.Log
import com.adyen.checkout.base.model.payments.request.PaymentComponentData
import com.adyen.checkout.base.model.payments.response.Action
import com.adyen.checkout.core.log.LogUtil
import com.adyen.checkout.core.model.JsonUtils
import com.adyen.checkout.dropin.service.CallResult
import com.adyen.checkout.dropin.service.DropInService
import com.adyen.checkout.example.api.CheckoutApiService
import com.github.adyenexample.api.model.createPaymentsRequest
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.IOException

/**
 * This is just an example on how to make network calls on the [DropInService].
 * You should make the calls to your own servers and have additional data or processing if necessary.
 */
class ExampleDropInService : DropInService() {

    companion object {
        private val TAG = LogUtil.getTag()
    }

    override fun makePaymentsCall(paymentComponentData: JSONObject): CallResult {
        val requestPayment = paymentComponentData.toString(JsonUtils.IDENT_SPACES)
        Log.d(TAG, "makePaymentsCall - $requestPayment")

        val requestJson = try {
            paymentsRequestJson(paymentComponentData)
        } catch (e: PaymentMethodIsNull) {
            return CallResult(CallResult.ResultType.ERROR, "Empty payment data")
        }

        val requestBody = RequestBody.create(MediaType.parse("application/json"), requestJson)

        val call = CheckoutApiService.INSTANCE.payments(requestBody)

        return try {
            val response = call.execute()
            val paymentsResponse = response.body()

            // Error body
            val byteArray = response.errorBody()?.bytes()
            if (byteArray != null) {
                val errorBody = String(byteArray)
                Log.e(TAG, "errorBody - $errorBody")
            }

            if (response.isSuccessful && paymentsResponse != null) {

                if (paymentsResponse.action != null) {
                    CallResult(CallResult.ResultType.ACTION, Action.SERIALIZER.serialize(paymentsResponse.action).toString())
                } else {
                    CallResult(CallResult.ResultType.FINISHED, paymentsResponse.resultCode ?: "EMPTY")
                }
            } else {
                Log.e(TAG, "FAILED - ${response.message()}")
                CallResult(CallResult.ResultType.ERROR, "IOException")
            }
        } catch (e: IOException) {
            Log.e(TAG, "IOException", e)
            CallResult(CallResult.ResultType.ERROR, "IOException")
        }
    }

    @Throws(PaymentMethodIsNull::class)
    private fun paymentsRequestJson(paymentComponentDataJson: JSONObject): String {

        val paymentComponentData = PaymentComponentData.SERIALIZER.deserialize(paymentComponentDataJson)

        if (paymentComponentData.paymentMethod == null) {
            throw PaymentMethodIsNull()
        }

        val paymentsRequest = createPaymentsRequest(this@ExampleDropInService, paymentComponentData)

        return Gson().toJson(paymentsRequest)
    }

    class PaymentMethodIsNull : Exception()

    override fun makeDetailsCall(actionComponentData: JSONObject): CallResult {
        Log.d(TAG, "makeDetailsCall")

        Log.v(TAG, "payments/details/ - ${actionComponentData.toString(JsonUtils.IDENT_SPACES)}")

        val requestBody = RequestBody.create(MediaType.parse("application/json"), actionComponentData.toString())
        val call = CheckoutApiService.INSTANCE.details(requestBody)

        return try {
            val response = call.execute()
            val detailsResponse = response.body()

            if (response.isSuccessful && detailsResponse != null) {
                if (detailsResponse.action != null) {
                    CallResult(CallResult.ResultType.ACTION, Action.SERIALIZER.serialize(detailsResponse.action).toString())
                } else {
                    CallResult(CallResult.ResultType.FINISHED, detailsResponse.resultCode ?: "EMPTY")
                }
            } else {
                Log.e(TAG, "FAILED - ${response.message()}")
                CallResult(CallResult.ResultType.ERROR, "IOException")
            }
        } catch (e: IOException) {
            Log.e(TAG, "IOException", e)
            CallResult(CallResult.ResultType.ERROR, "IOException")
        }
    }

}
