package com.example.mayank.kwizzapp.settings.menusettings

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import kwizzapp.com.kwizzapp.R
import kwizzapp.com.kwizzapp.settings.bankdetails.BankDetailFragment
import kwizzapp.com.kwizzapp.settings.policies.PoliciesFragment
import kwizzapp.com.kwizzapp.settings.profilesettings.ProfileFragment
import kwizzapp.com.kwizzapp.viewmodels.SettingVm
import net.rmitsolutions.mfexpert.lms.helpers.switchToFragment
import net.rmitsolutions.mfexpert.lms.helpers.switchToFragmentBackStack
import org.jetbrains.anko.find

class SettingMenuViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {

    fun bindView(context: Context, settingMenuVm: SettingVm.SettingMenuVm, position: Int){
        val textTitle = itemView.find<TextView>(R.id.setting_header_name)
        val imageIcon = itemView.find<ImageView>(R.id.setting_icon)

        textTitle.text = settingMenuVm.title
        imageIcon.setImageResource(settingMenuVm.imageSource)

        itemView.setOnClickListener{
            when(position){
                0 ->{
                    val profileFragment = ProfileFragment()
                    context.switchToFragmentBackStack(profileFragment)
                }
                1 ->{
                    val policiesFragment = PoliciesFragment()
                    context.switchToFragmentBackStack(policiesFragment)
                }
            }
        }
    }
}