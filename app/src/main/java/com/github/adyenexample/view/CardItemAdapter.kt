package com.github.adyenexample.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.adyenexample.R
import com.github.adyenexample.models.CardItem

class CardItemAdapter : RecyclerView.Adapter<CardItemAdapter.ItemViewHolder>() {

    var onCardItemClickListener: OnCardItemClickListener? = null

    private val list = mutableListOf<CardItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.view_payment_card, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val cardItemView = holder.itemView as CardItemView
        cardItemView.onCardItemClickListener = onCardItemClickListener
        cardItemView.renderView(list[position])
    }

    fun setList(source: List<CardItem>?) {
        source?.let {
            list.addAll(it)
            this@CardItemAdapter.notifyDataSetChanged()
        }
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}