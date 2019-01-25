package com.example.mayank.kwizzapp.gameresult.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.technoholicdeveloper.kwizzapp.result.adapter.ResultViewHolder
import kwizzapp.com.kwizzapp.R
import kwizzapp.com.kwizzapp.viewmodels.ResultViewModel

class ResultViewAdapter: RecyclerView.Adapter<ResultViewHolder>() {

    var items: List<ResultViewModel.MultiplayerResultVm> = emptyList()
    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        context = parent.context
        val v = LayoutInflater.from(context).inflate(R.layout.multiplayer_result_row, parent, false)
        return ResultViewHolder(v)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        holder.bindView(context,items[position], position)
    }
}