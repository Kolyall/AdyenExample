package com.github.adyenexample

import android.util.Log
import android.widget.Toast
import com.adyen.checkout.base.model.payments.request.PaymentComponentData
import com.adyen.checkout.base.model.payments.request.PaymentMethodDetails
import com.adyen.checkout.redirect.RedirectComponent
import com.github.adyenexample.ext.SchedulersProvider
import com.github.adyenexample.ext.async
import com.github.adyenexample.ext.createPaymentMethodsRequest
import com.github.adyenexample.ext.createPaymentsRequest
import com.github.adyenexample.ext.getAmount
import com.github.adyenexample.ext.subscribeAndAdd
import com.github.adyenexample.models.CardItem
import com.github.adyenexample.models.toApi
import com.github.adyenexample.models.toViewModel
import com.github.kolyall.adyen.RxApiServiceCheckout
import com.github.kolyall.adyen.model.ApiAdditionalData
import com.github.kolyall.adyen.model.ApiPaymentsRequest
import com.google.gson.Gson
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject


class MainActivityPresenter @Inject constructor(
    private val gson: Gson,
    private val rxApiServiceCheckout: RxApiServiceCheckout,
    private val schedulersProvider: SchedulersProvider
) {

    lateinit var view: MainActivity
    private val disposables: CompositeDisposable = CompositeDisposable()

    val TAG: String = "MainActivityPresenter"

    fun makeRecurentPayment(cardItem: CardItem) {

        val paymentMethod = com.github.kolyall.adyen.model.ApiRecurentPaymentMethod()
        paymentMethod.type = "scheme"
        paymentMethod.storedPaymentMethodId = cardItem.id
        val paymentsRequest = ApiPaymentsRequest(
            paymentMethod = paymentMethod,
            shopperReference = "shopperReferenceId",
            amount = getAmount(),
            merchantAccount = BuildConfig.MERCHANT_ACCOUNT,
            returnUrl = RedirectComponent.getReturnUrl(view),
            additionalData = ApiAdditionalData(),
            storePaymentMethod = false,
            recurringProcessingModel = "Subscription",
            shopperInteraction = "ContAuth"
        )

        rxApiServiceCheckout.payments(paymentsRequest)
            .async(schedulersProvider)
            .doOnSuccess { response ->
                Log.d(TAG, "doOnSuccess: $response")
            }
            .subscribeAndAdd(disposables)
    }

    fun getPaymentMethods() {
        val request = createPaymentMethodsRequest("shopperReferenceId")
        rxApiServiceCheckout.paymentMethods(request)
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

    fun makePayment(paymentComponentData: PaymentComponentData<PaymentMethodDetails>) {
        if (paymentComponentData.paymentMethod == null) {
            Toast.makeText(view, "paymentMethod is Not Valid", Toast.LENGTH_SHORT).show()
            return
        }

        val paymentMethod = paymentComponentData.paymentMethod as PaymentMethodDetails
        val paymentsRequest = createPaymentsRequest(view, paymentMethod.toApi(gson), paymentComponentData.isStorePaymentMethodEnable, "shopperReferenceId")

        rxApiServiceCheckout.payments(paymentsRequest)
            .async(schedulersProvider)
            .doOnSuccess { response ->
                Log.d(TAG, "doOnSuccess: $response")
                Toast.makeText(view, response.resultCode, Toast.LENGTH_SHORT).show()
            }
            .subscribeAndAdd(disposables)
    }

    fun onDestroy() {
        disposables.clear()
    }

}
