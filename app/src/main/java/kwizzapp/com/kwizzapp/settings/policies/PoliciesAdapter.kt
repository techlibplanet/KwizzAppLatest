package com.example.mayank.kwizzapp.policies

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import kwizzapp.com.kwizzapp.databinding.PolicyItemBinding
import kwizzapp.com.kwizzapp.settings.policies.PolicyViewHolder
import kwizzapp.com.kwizzapp.viewmodels.SettingVm

class PoliciesAdapter: RecyclerView.Adapter<PolicyViewHolder>() {

    var items: List<SettingVm.SettingMenuVm> = emptyList()
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PolicyViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(context)
        val dataBinding = PolicyItemBinding.inflate(inflater, parent, false)
        return PolicyViewHolder(dataBinding)

    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: PolicyViewHolder, position: Int) {
        val policies = items[position]
        holder.bindView(context,policies, position)
    }
}