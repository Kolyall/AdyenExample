package com.github.adyenexample

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adyen.checkout.base.PaymentComponentState
import com.adyen.checkout.base.model.payments.request.PaymentMethodDetails
import com.adyen.checkout.base.util.PaymentMethodTypes
import com.adyen.checkout.card.CardComponent
import com.adyen.checkout.card.CardConfiguration
import com.adyen.checkout.card.CardView
import com.adyen.checkout.core.api.Environment
import com.github.adyenexample.models.CardItem
import com.github.adyenexample.view.CardItemAdapter
import com.github.kolyall.adyen.mapper.toAdyen
import com.github.kolyall.adyen.model.ApiPaymentMethodsApiResponse
import com.google.gson.Gson
import dagger.android.AndroidInjection
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var presenter: MainActivityPresenter

    @Inject
    lateinit var gson: Gson

    private var componentState: PaymentComponentState<out PaymentMethodDetails>? = null
    private lateinit var adyenCardView: CardView
    private lateinit var submitButton: AppCompatButton
    private lateinit var recyclerView: RecyclerView
    private val adapter = CardItemAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        presenter.view = this
        setContentView(R.layout.activity_main)

        adyenCardView = findViewById(R.id.adyenCardView)
        submitButton = findViewById(R.id.submitButton)
        recyclerView = findViewById(R.id.recyclerView)

        setupRecyclerView()
        presenter.getPaymentMethods()
    }

    private fun setupRecyclerView() {
        adapter.onCardItemClickListener = {
            presenter.makeRecurrentPayment(it)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    fun onGetPaymentMethods(response: ApiPaymentMethodsApiResponse) {
        addAdyenCardView(response)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()

    }

    private fun addAdyenCardView(response: ApiPaymentMethodsApiResponse) {
        val paymentMethod = response.paymentMethods?.firstOrNull { it.type.equals(PaymentMethodTypes.SCHEME) }
            ?: throw NullPointerException("paymentMethod with type not found \"${PaymentMethodTypes.SCHEME}\"")

        val cardConfiguration =
            CardConfiguration.Builder(this, BuildConfig.PUBLIC_KEY)
                .apply { setEnvironment(Environment.TEST) }
                .setShopperReference(BuildConfig.SHOPPER_REFERENCE)
                .setHolderNameRequire(true)
                .setShowStorePaymentField(true)
                .build()

        val cardComponent = CardComponent.PROVIDER.get(this, paymentMethod.toAdyen(gson), cardConfiguration)


        // Replace CardComponent with the payment method Component that you want to add.
        // See list of Supported payment methods at https://docs.adyen.com/checkout/android/components#supported-payment-methods

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
                presenter.makePayment(paymentComponentData)
            }
        }
    }

    fun onStoredPaymentMethods(list: List<CardItem>?) {
        adapter.setList(list)
    }

    fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

}
