package kwizzapp.com.kwizzapp.multiplayer.adapter.pointsadapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import kwizzapp.com.kwizzapp.R
import kwizzapp.com.kwizzapp.viewmodels.ResultViewModel

class PointsViewAdapter: RecyclerView.Adapter<PointsViewHolder>() {

    var items: List<ResultViewModel.MultiplayerPointsVm> = emptyList()
    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PointsViewHolder{
        context = parent.context
        val v = LayoutInflater.from(context).inflate(R.layout.multiplayer_points_row, parent, false)
        return PointsViewHolder(v)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: PointsViewHolder, position: Int) {
        holder.bindView(context,items[position], position)
    }
}