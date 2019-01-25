package com.example.mayank.kwizzapp.settings.menusettings

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import kwizzapp.com.kwizzapp.R
import kwizzapp.com.kwizzapp.viewmodels.SettingVm

class SettingMenuAdapter: RecyclerView.Adapter<SettingMenuViewHolder>() {

    var items: List<SettingVm.SettingMenuVm> = emptyList()
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingMenuViewHolder {
        context = parent.context
        val v = LayoutInflater.from(context).inflate(R.layout.setting_menu_row, parent, false)
        return SettingMenuViewHolder(v)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: SettingMenuViewHolder, position: Int) {
        val settingMenu = items[position]
        holder.bindView(context, settingMenu, position)
    }
}