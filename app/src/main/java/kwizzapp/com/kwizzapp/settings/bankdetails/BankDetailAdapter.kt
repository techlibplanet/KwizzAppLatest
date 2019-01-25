package com.example.mayank.kwizzapp.bankdetail

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import kwizzapp.com.kwizzapp.databinding.BankDetailBinding
import kwizzapp.com.kwizzapp.viewmodels.SettingVm

class BankDetailAdapter: RecyclerView.Adapter<BankDetailViewHolder>() {

    var items: List<SettingVm.BankDetailVm> = emptyList()
    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BankDetailViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(context)
        val dataBinding =  BankDetailBinding.inflate(inflater, parent, false)
//        val v = LayoutInflater.from(context).inflate(R.layout.profile_row, parent, false)
        return BankDetailViewHolder(dataBinding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: BankDetailViewHolder, position: Int) {
        val bankDetailsVm = items[position]
        holder.bindView(bankDetailsVm)
    }
}