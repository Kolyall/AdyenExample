package com.github.adyenexample.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.github.adyenexample.R
import com.github.adyenexample.models.CardItem

class CardItemView : LinearLayout, View.OnClickListener {
    private lateinit var item: CardItem
    var id: TextView

    var brand: TextView
    var holderName: TextView
    var lastFour: TextView
    var expiry: TextView

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        inflate(context, R.layout.view_item_payment_card, this)

        id = findViewById(R.id.id)
        brand = findViewById(R.id.brand)
        holderName = findViewById(R.id.holderName)
        lastFour = findViewById(R.id.lastFour)
        expiry = findViewById(R.id.expiry)
        setOnClickListener(this)
    }

    fun renderView(cardItem: CardItem) {
        item = cardItem
        id.text = cardItem.id
        brand.text = cardItem.brand
        holderName.text = cardItem.holderName
        lastFour.text = cardItem.lastFour
        expiry.text = cardItem.expiryMonth + "/" + cardItem.expiryYear
    }

    var onCardItemClickListener: OnCardItemClickListener? = null

    override fun onClick(view: View?) {
        onCardItemClickListener?.invoke(item)
    }

}

typealias OnCardItemClickListener = (CardItem) -> Unit