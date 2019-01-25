package kwizzapp.com.kwizzapp.multiplayer.adapter.pointsadapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.common.images.ImageManager
import kwizzapp.com.kwizzapp.R
import kwizzapp.com.kwizzapp.viewmodels.ResultViewModel

class PointsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindView(context : Context, pointsViewModel: ResultViewModel.MultiplayerPointsVm, position: Int){
        val textViewPlayerName = itemView.findViewById<TextView>(R.id.textViewPlayerName)
        val textViewPointsBid = itemView.findViewById<TextView>(R.id.textViewPointsBid)
        val textViewPointsLoose = itemView.findViewById<TextView>(R.id.textViewPointsLoose)
        val textViewPointsWin = itemView.findViewById<TextView>(R.id.textViewPointsWin)
        val textViewTotalPoints = itemView.findViewById<TextView>(R.id.textViewTotal)
        val textViewScore = itemView.findViewById<TextView>(R.id.textViewScore)
        val imageViewProfile = itemView.findViewById<ImageView>(R.id.playerDisplayImage)

        textViewPlayerName.text = pointsViewModel.playerName
        textViewPointsBid.text = pointsViewModel.pointsBid.toString()
        textViewPointsLoose.text = pointsViewModel.pointsLoose.toString()
        textViewPointsWin.text = pointsViewModel.pointsWin.toString()
        textViewTotalPoints.text = pointsViewModel.totalPoints.toString()
        textViewScore.text = pointsViewModel.score.toString()
//        imageViewProfile.setImageResource(pointsViewModel.imageUri!!)

        val mgr = ImageManager.create(context)
        mgr.loadImage(imageViewProfile, pointsViewModel.imageUri)
    }
}