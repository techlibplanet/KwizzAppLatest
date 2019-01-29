package com.example.mayank.kwizzapp.profile

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import kwizzapp.com.kwizzapp.R
import kwizzapp.com.kwizzapp.databinding.ProfileDetailBinding
import kwizzapp.com.kwizzapp.viewmodels.SettingVm

class ProfileViewAdapter: RecyclerView.Adapter<ProfileViewHolder>() {

    var items: List<SettingVm.ProfileVm> = emptyList()
    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(context)
        val dataBinding = ProfileDetailBinding.inflate(inflater, parent, false)
        return ProfileViewHolder(dataBinding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        val profileVm = items[position]
        holder.bindView(profileVm)
    }
}