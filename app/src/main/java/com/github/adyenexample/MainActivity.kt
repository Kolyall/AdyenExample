package com.github.adyenexample

import android.os.Bundle
import android.util.Log
import android.view.ViewStub
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.Observer
import com.adyen.checkout.base.PaymentComponentState
import com.adyen.checkout.base.model.PaymentMethodsApiResponse
import com.adyen.checkout.base.model.payments.request.PaymentMethodDetails
import com.adyen.checkout.base.util.PaymentMethodTypes
import com.adyen.checkout.card.CardComponent
import com.adyen.checkout.card.CardConfiguration
import com.adyen.checkout.card.CardView
import com.adyen.checkout.core.api.Environment
import com.github.adyenexample.api.RxApiServiceCheckout
import com.github.adyenexample.api.model.createPaymentMethodsRequest
import com.github.adyenexample.api.model.createPaymentsRequest
import dagger.android.AndroidInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private val disposables: CompositeDisposable = CompositeDisposable()

    @Inject
    lateinit var rxApiServiceCheckout: RxApiServiceCheckout

    private var componentState: PaymentComponentState<PaymentMethodDetails>? = null
    private lateinit var adyenCardView: CardView
    private lateinit var adyenCardViewViewStub: ViewStub
    private lateinit var submitButton: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adyenCardViewViewStub = findViewById(R.id.adyenCardViewViewStub)
        submitButton = findViewById(R.id.submitButton)

        getPaymentMethods()
    }

    private fun getPaymentMethods() {
        val request = createPaymentMethodsRequest(this)
        rxApiServiceCheckout.paymentMethods(request)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { response ->
                Log.d(TAG, "doOnSuccess: $response")
                onGetPaymentMethods(response)
            }
            .subscribe({}, {}, {})
            .addToDisposables(disposables)
    }

    private fun onGetPaymentMethods(response: PaymentMethodsApiResponse) {
        addAdyenCardView(response)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

    private fun addAdyenCardView(response: PaymentMethodsApiResponse) {
        val paymentMethod = response.paymentMethods?.firstOrNull { it.type.equals(PaymentMethodTypes.SCHEME) }
            ?: throw NullPointerException("paymentMethod with type not found \"${PaymentMethodTypes.SCHEME}\"")

        val cardConfiguration =
            CardConfiguration.Builder(this, BuildConfig.PUBLIC_KEY)
                .apply { setEnvironment(Environment.TEST) }
                .setShopperReference(BuildConfig.SHOPPER_REFERENCE)
                .setHolderNameRequire(true)
                .setShowStorePaymentField(true)
                .build()

        val cardComponent = CardComponent.PROVIDER.get(this, paymentMethod, cardConfiguration)


        // Replace CardComponent with the payment method Component that you want to add.
        // See list of Supported payment methods at https://docs.adyen.com/checkout/android/components#supported-payment-methods

        val inflate = adyenCardViewViewStub.inflate()
        adyenCardView = inflate as CardView
        adyenCardView.attach(cardComponent, this)

        cardComponent.observe(this@MainActivity, Observer { componentState ->
            this@MainActivity.componentState = componentState
            if (componentState?.isValid == true) {
                // When the proceeds to pay, pass the `paymentComponentState.data` to your server to send a /payments request
                Toast.makeText(this@MainActivity, "Payment is Valid", Toast.LENGTH_SHORT).show()
            }
            submitButton.isEnabled = componentState?.isValid == true
            // Replace CardComponent with the payment method Component that you want to add.
            // See list of Supported payment methods at https://docs.adyen.com/checkout/android/components#supported-payment-methods
        })

        submitButton.setOnClickListener {
            val componentState = componentState
            if (componentState != null && componentState.isValid) {
                val paymentComponentData = componentState.data

                if (paymentComponentData.paymentMethod == null) {
                    Toast.makeText(this@MainActivity, "paymentMethod is Not Valid", Toast.LENGTH_SHORT).show()
                }

                val paymentsRequest = createPaymentsRequest(this, paymentComponentData)

                rxApiServiceCheckout.payments(paymentsRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSuccess { response ->
                        Log.d(TAG, "doOnSuccess: $response")
                        Toast.makeText(this@MainActivity, response.resultCode, Toast.LENGTH_SHORT).show()
                    }
                    .subscribe({}, {}, {})
                    .addToDisposables(disposables)
            }
        }
    }

    val TAG: String = "MainActivity"

}

private fun Disposable.addToDisposables(disposables: CompositeDisposable) {
    disposables.add(this)
}
