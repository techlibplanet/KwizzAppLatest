package com.example.mayank.kwizzapp.transactions

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import kwizzapp.com.kwizzapp.R

class TransactionAdapter: RecyclerView.Adapter<TransactionDetailViewHolder>() {

    var items: List<TransactionDetailsVm> = emptyList()
    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionDetailViewHolder{
        context = parent.context
        val inflater = LayoutInflater.from(context)
//        val dataBinding = TransactionRowBinding.inflate(inflater, parent, false)
        val v = LayoutInflater.from(context).inflate(R.layout.transaction_row, parent, false)
        return TransactionDetailViewHolder(v)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: TransactionDetailViewHolder, position: Int) {
        val transactionVm = items[position]
        holder.bindView(context ,transactionVm)
    }
}