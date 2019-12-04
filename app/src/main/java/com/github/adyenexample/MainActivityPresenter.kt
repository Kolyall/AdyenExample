package com.github.adyenexample

import android.util.Log
import android.widget.Toast
import com.adyen.checkout.base.model.payments.request.PaymentComponentData
import com.adyen.checkout.base.model.payments.request.PaymentMethodDetails
import com.github.adyenexample.ext.SchedulersProvider
import com.github.adyenexample.ext.async
import com.github.adyenexample.ext.subscribeAndAdd
import com.github.adyenexample.models.CardItem
import com.github.adyenexample.models.toApi
import com.github.adyenexample.models.toViewModel
import com.github.kolyall.adyen.AdyenConfig
import com.github.kolyall.adyen.AdyenService
import com.github.kolyall.adyen.model.ApiAdditionalData
import com.github.kolyall.adyen.model.ApiPaymentMethodsRequest
import com.github.kolyall.adyen.model.ApiPaymentsRequest
import com.github.kolyall.adyen.model.ApiRecurentPaymentMethod
import com.google.gson.Gson
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject


class MainActivityPresenter
@Inject constructor(
    private val adyenConfig: AdyenConfig,
    private val gson: Gson,
    private val adyenService: AdyenService,
    private val schedulersProvider: SchedulersProvider
) {

     val DEFAULT_COUNTRY = "NL"
     val DEFAULT_LOCALE = "en_US"

    lateinit var view: MainActivity
    private val disposables: CompositeDisposable = CompositeDisposable()

    val TAG: String = "MainActivityPresenter"

    fun getPaymentMethods() {
        val request = ApiPaymentMethodsRequest(
            merchantAccount = adyenConfig.merchantAccount,
            shopperReference = "shopperReferenceId",
            amount = adyenConfig.defaultAmount,
            countryCode = DEFAULT_COUNTRY,
            shopperLocale = DEFAULT_LOCALE
        )
        adyenService.paymentMethods(request)
            .async(schedulersProvider)
            .doOnSuccess { response ->
                val storedPaymentMethods = response.storedPaymentMethods
                val list = storedPaymentMethods?.map { it.toViewModel() }
                Log.d(TAG, "doOnSuccess: $response")
                view.onStoredPaymentMethods(list)
                view.onGetPaymentMethods(response)
            }
            .subscribeAndAdd(disposables)
    }

    fun makePayment(paymentComponentData: PaymentComponentData<out PaymentMethodDetails>) {
        if (paymentComponentData.paymentMethod == null) {
            Toast.makeText(view, "paymentMethod is Not Valid", Toast.LENGTH_SHORT).show()
            return
        }

        val paymentMethod = paymentComponentData.paymentMethod as PaymentMethodDetails

        val request = ApiPaymentsRequest(
            paymentMethod = paymentMethod.toApi(gson),
            shopperReference = "shopperReferenceId",
            storePaymentMethod = paymentComponentData.isStorePaymentMethodEnable,
            amount = adyenConfig.defaultAmount,
            merchantAccount = adyenConfig.merchantAccount,
            returnUrl = adyenConfig.returnUrl,
            additionalData = ApiAdditionalData()
        )

        adyenService.payments(request)
            .async(schedulersProvider)
            .doOnSuccess { response ->
                Log.d(TAG, "doOnSuccess: $response")
                Toast.makeText(view, response.resultCode, Toast.LENGTH_SHORT).show()
            }
            .subscribeAndAdd(disposables)
    }

    fun makeRecurrentPayment(cardItem: CardItem) {

        val paymentMethod = ApiRecurentPaymentMethod().apply { type = "scheme"
            storedPaymentMethodId = cardItem.id }

        val paymentsRequest = ApiPaymentsRequest(
            paymentMethod = paymentMethod,
            shopperReference = "shopperReferenceId",
            amount = adyenConfig.defaultAmount,
            merchantAccount = adyenConfig.merchantAccount,
            returnUrl = adyenConfig.returnUrl,
            additionalData = ApiAdditionalData(),
            storePaymentMethod = false,
            recurringProcessingModel = "Subscription",
            shopperInteraction = "ContAuth"
        )

        adyenService.payments(paymentsRequest)
            .async(schedulersProvider)
            .doOnSuccess { response ->
                Log.d(TAG, "doOnSuccess: $response")
                view.showToast("Success Recurrent Payment\n$response")
            }
            .subscribeAndAdd(disposables)
    }

    fun onDestroy() {
        disposables.clear()
    }

}
