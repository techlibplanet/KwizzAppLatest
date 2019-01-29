package com.example.mayank.kwizzapp.settings.menusettings

import android.content.Context
import android.support.v7.widget.RecyclerView
import kwizzapp.com.kwizzapp.databinding.SettingMenuBinding
import kwizzapp.com.kwizzapp.settings.policies.PoliciesFragment
import kwizzapp.com.kwizzapp.settings.profilesettings.ProfileFragment
import kwizzapp.com.kwizzapp.viewmodels.SettingVm
import net.rmitsolutions.mfexpert.lms.helpers.switchToFragmentBackStack

class SettingMenuViewHolder(val dataBinding: SettingMenuBinding) : RecyclerView.ViewHolder(dataBinding.root) {

    private lateinit var context: Context

    fun bindView(context: Context, policyVm: SettingVm.SettingMenuVm, position: Int) {
        this.context = context
        dataBinding.settingMenuVm = policyVm

        dataBinding.root.setOnClickListener {
            when (position) {
                0 -> {
                    val profileFragment = ProfileFragment()
                    context.switchToFragmentBackStack(profileFragment)
                }
                1 -> {
                    val policiesFragment = PoliciesFragment()
                    context.switchToFragmentBackStack(policiesFragment)
                }
            }
        }
    }
}
