package com.github.adyenexample

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.Observer
import com.adyen.checkout.base.PaymentComponentState
import com.adyen.checkout.base.model.PaymentMethodsApiResponse
import com.adyen.checkout.base.model.paymentmethods.PaymentMethod
import com.adyen.checkout.base.model.payments.request.PaymentComponentData
import com.adyen.checkout.base.model.payments.request.PaymentMethodDetails
import com.adyen.checkout.base.util.PaymentMethodTypes
import com.adyen.checkout.card.CardComponent
import com.adyen.checkout.card.CardConfiguration
import com.adyen.checkout.card.CardView
import com.adyen.checkout.core.api.Environment
import com.adyen.checkout.dropin.DropIn
import com.adyen.checkout.dropin.DropInConfiguration
import com.adyen.checkout.dropin.service.DropInService
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private var componentState: PaymentComponentState<PaymentMethodDetails>? = null
    lateinit var adyenCardView: CardView
    lateinit var submitButton: AppCompatButton
    lateinit var dropInPay: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adyenCardView = findViewById(R.id.adyenCardView)
        submitButton = findViewById(R.id.submitButton)
        dropInPay = findViewById(R.id.dropInPay)

        val paymentMethodsResponse = JSONObject(getString(R.string.json_response))
        val paymentMethodsApiResponse = PaymentMethodsApiResponse.SERIALIZER.deserialize(paymentMethodsResponse)

        val builder = CardConfiguration.Builder(this, BuildConfig.PUBLIC_KEY)
        builder
            .setEnvironment(Environment.TEST)
        val cardConfiguration =
            builder
                .setShopperReference(BuildConfig.SHOPPER_REFERENCE)
                .setHolderNameRequire(true)
                .setShowStorePaymentField(true)
                // When you're ready to accept live payments, change the value to one of our live environments.
                .build()
        // Create the configuration for the payment method that you want to add.

        val paymentMethod = paymentMethodsApiResponse.paymentMethods?.firstOrNull { it.type.equals(PaymentMethodTypes.SCHEME) }
            ?: throw NullPointerException("paymentMethod with type not found \"${PaymentMethodTypes.SCHEME}\"")
        addAdyenCardView(paymentMethod, cardConfiguration)

//        val bcmcConfiguration =
//            BcmcConfiguration.Builder(
//                this@MainActivity,
//                BuildConfig.PUBLIC_KEY
//            )
//                .build()

//        val googlePayConfig = GooglePayConfiguration.Builder(
//            this@MainActivity,
//            BuildConfig.MERCHANT_ACCOUNT
//        ).build()

        val resultIntent = Intent(this, MainActivity::class.java)
        resultIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

        val dropInConfiguration = DropInConfiguration.Builder(
            this@MainActivity, resultIntent, ExampleDropInService::class.java
        )
            .addCardConfiguration(cardConfiguration)
//            .addBcmcConfiguration(bcmcConfiguration)
//            .addGooglePayConfiguration(googlePayConfig)
            .build()

        dropInPay.setOnClickListener {
            DropIn.startPayment(this@MainActivity, paymentMethodsApiResponse, dropInConfiguration)
        }

    }

    private fun addAdyenCardView(paymentMethod: PaymentMethod, cardConfiguration: CardConfiguration) {
        val cardComponent = CardComponent.PROVIDER.get(this, paymentMethod, cardConfiguration)


        // Replace CardComponent with the payment method Component that you want to add.
        // See list of Supported payment methods at https://docs.adyen.com/checkout/android/components#supported-payment-methods

        adyenCardView.attach(cardComponent, this)

        cardComponent.observe(this@MainActivity, Observer { componentState ->
            this@MainActivity.componentState = componentState
            if (componentState?.isValid == true) {
                // When the proceeds to pay, pass the `paymentComponentState.data` to your server to send a /payments request
                Toast.makeText(this@MainActivity, "payment is Valid", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainActivity, "payment is Not Valid", Toast.LENGTH_SHORT).show()
            }
            // Replace CardComponent with the payment method Component that you want to add.
            // See list of Supported payment methods at https://docs.adyen.com/checkout/android/components#supported-payment-methods
        })

        submitButton.setOnClickListener {
            val componentState = componentState
            if (componentState != null && componentState.isValid) {
                sendPaymentRequest(componentState.data)
            }
        }
    }

    private fun sendPaymentRequest(data: PaymentComponentData<PaymentMethodDetails>) {
        Log.e(TAG, "sendPaymentRequest (line 107): $data " )

        val merchantService = ComponentName(packageName, ExampleDropInService::class.java.name)
        DropInService.requestPaymentsCall(this, data, merchantService)
    }

    val TAG: String = "MainActivity"

}
