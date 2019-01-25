package com.example.mayank.kwizzapp.profile

import android.support.v7.widget.RecyclerView
import kwizzapp.com.kwizzapp.databinding.ProfileDetailBinding
import kwizzapp.com.kwizzapp.viewmodels.SettingVm

class ProfileViewHolder(val dataBinding: ProfileDetailBinding) : RecyclerView.ViewHolder(dataBinding.root) {

    fun bindView(profileDetailVm: SettingVm.ProfileVm) {
        dataBinding.profileDetailVm = profileDetailVm
    }
}