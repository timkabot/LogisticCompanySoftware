package com.example.timkabor.finallogisticcompany.ui.adapters

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.timkabor.finallogisticcompany.models.DispatchOrder
import com.example.timkabor.finallogisticcompany.R
import kotlinx.android.synthetic.main.card_orders.view.*

class OrderListAdapter(val orderList: List<DispatchOrder>, val clickListener: (DispatchOrder) -> Unit) : RecyclerView.Adapter<OrderListAdapter.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderListAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_orders, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: OrderListAdapter.ViewHolder, position: Int) {
        holder.bindItems(orderList[position], clickListener)
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return orderList.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bindItems(order: DispatchOrder, listener: (DispatchOrder) -> Unit) {
            itemView.sourceCity.text = "From : " + order.source
            itemView.destinationCity.text = "To : " + order.destination
            itemView.orderId.text = "Order #" + order.id
            itemView.tag = order.id
            itemView.setOnClickListener { listener(order) }
        }
    }
}