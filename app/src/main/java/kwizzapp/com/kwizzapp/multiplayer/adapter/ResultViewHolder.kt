package com.technoholicdeveloper.kwizzapp.result.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.common.images.ImageManager
import kwizzapp.com.kwizzapp.R
import kwizzapp.com.kwizzapp.viewmodels.ResultViewModel


class ResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindView(context : Context, resultViewModel: ResultViewModel.MultiplayerResultVm, position: Int){
        val textViewPlayerName = itemView.findViewById<TextView>(R.id.textViewPlayerName)
        val textViewRightAnswer = itemView.findViewById<TextView>(R.id.textViewRightAnswer)
        val imageViewProfile = itemView.findViewById<ImageView>(R.id.playerDisplayImage)

        textViewPlayerName.text = resultViewModel.playerName
        textViewRightAnswer.text = resultViewModel.rightAnswers.toString()

        val mgr = ImageManager.create(context)
        mgr.loadImage(imageViewProfile, resultViewModel.imageUri)


    }
}