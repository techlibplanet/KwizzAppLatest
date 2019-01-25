package com.example.mayank.kwizzapp.bankdetail

import android.support.v7.widget.RecyclerView
import kwizzapp.com.kwizzapp.databinding.BankDetailBinding
import kwizzapp.com.kwizzapp.viewmodels.SettingVm


class BankDetailViewHolder(val dataBinding: BankDetailBinding) : RecyclerView.ViewHolder(dataBinding.root) {

    fun bindView(bankDetailVm: SettingVm.BankDetailVm) {
        dataBinding.bankDetailVm = bankDetailVm
    }
}
