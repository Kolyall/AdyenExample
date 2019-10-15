package com.github.adyenexample

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adyen.checkout.base.PaymentComponentState
import com.adyen.checkout.base.util.PaymentMethodTypes
import com.adyen.checkout.card.CardComponent
import com.adyen.checkout.card.CardConfiguration
import com.adyen.checkout.card.CardView
import com.adyen.checkout.core.api.Environment
import com.github.adyenexample.models.CardItem
import com.github.adyenexample.models.toAdyen
import com.github.adyenexample.view.CardItemAdapter
import com.github.kolyall.adyen.model.ApiPaymentMethodsApiResponse
import com.google.gson.Gson
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrListener
import com.r0adkll.slidr.model.SlidrPosition
import dagger.android.AndroidInjection
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var presenter: MainActivityPresenter

    @Inject
    lateinit var gson: Gson

    private var componentState: PaymentComponentState<com.adyen.checkout.base.model.payments.request.PaymentMethodDetails>? = null
    private lateinit var adyenCardView: CardView
    private lateinit var submitButton: AppCompatButton
    private lateinit var recyclerView: RecyclerView
    private val adapter = CardItemAdapter()

    private val layoutId = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        presenter.view = this
        setContentView(layoutId)
        setupSlidr(SlidrPosition.LEFT)

        adyenCardView = findViewById(R.id.adyenCardView)
        submitButton = findViewById(R.id.submitButton)
        recyclerView = findViewById(R.id.recyclerView)

        setupRecyclerView()
        presenter.getPaymentMethods()
    }

    private fun setupRecyclerView() {
        adapter.onCardItemClickListener = {
            presenter.makeRecurentPayment(it)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }



    protected fun setupSlidr(slidrPosition: SlidrPosition) {
        val config = slidrConfig(slidrPosition)

        Slidr.attach(this, config)
    }

    open fun slidrConfig(slidrPosition: SlidrPosition): SlidrConfig {
        return SlidrConfig.Builder()
            .primaryColor(resources.getColor(android.R.color.transparent))
            .secondaryColor(resources.getColor(android.R.color.transparent))
            .position(slidrPosition)
            .sensitivity(1.0f)
            .scrimColor(ContextCompat.getColor(this, R.color.appBlack))
            .scrimStartAlpha(0.8f)
            .scrimEndAlpha(0f)
            .velocityThreshold(2400f)
            .distanceThreshold(0.25f)
            .listener(object : SlidrListener {
                override fun onSlideStateChanged(state: Int) {

                }

                override fun onSlideChange(percent: Float) {
                }

                override fun onSlideOpened() {

                }

                override fun onSlideClosed(): Boolean {
                    return false
                }
            })
            .edge(false)
            .edgeSize(0.18f) // The % of the screen that counts as the edge, default 18%
            .build()
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

}
