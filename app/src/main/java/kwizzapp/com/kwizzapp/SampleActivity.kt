package kwizzapp.com.kwizzapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import org.jetbrains.anko.find


class SampleActivity : AppCompatActivity() {


//    private lateinit var resultRecyclerView: RecyclerView
//    private lateinit var pointsRecyclerView: RecyclerView
//    private lateinit var listResult: MutableList<ResultViewModel.MultiplayerResultVm>
//    private lateinit var listPoints: MutableList<ResultViewModel.MultiplayerPointsVm>
//    val adapter: ResultViewAdapter by lazy { ResultViewAdapter() }
//    val adapterPoints: PointsViewAdapter by lazy { PointsViewAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        val imageView = findViewById<View>(R.id.app_logo) as ImageView
        val appName = find<TextView>(R.id.app_name)
        val appDesc = find<TextView>(R.id.app_desc)
        val animLayout = find<LinearLayout>(R.id.anim_layout)

        val animationLogo = AnimationUtils.loadAnimation(applicationContext, R.anim.zoom_out)
        val animationText = AnimationUtils.loadAnimation(applicationContext, R.anim.zoom_out)
        val animationDesc = AnimationUtils.loadAnimation(applicationContext, R.anim.zoom_out)

        animLayout.startAnimation(animationLogo)
//        imageView.startAnimation(animationLogo)
//        appName.startAnimation(animationText)
//        appDesc.startAnimation(animationDesc)

//        listResult = mutableListOf<ResultViewModel.MultiplayerResultVm>()
//        listPoints = mutableListOf<ResultViewModel.MultiplayerPointsVm>()
//
//        resultRecyclerView = findViewById(R.id.result_recycler_view)
//        resultRecyclerView.layoutManager = LinearLayoutManager(this)
//        resultRecyclerView.setHasFixedSize(true)
//        resultRecyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
//        resultRecyclerView.adapter = adapter
//
//        resultRecyclerView = findViewById(R.id.points_recycler_view)
//        resultRecyclerView.layoutManager = LinearLayoutManager(this)
//        resultRecyclerView.setHasFixedSize(true)
//        resultRecyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
//        resultRecyclerView.adapter = adapterPoints
//        setItem()
//
//        setPoints()
    }

//    private fun setPoints() {
//        listPoints.clear()
//        listPoints.add(ResultViewModel.MultiplayerPointsVm("Mayank Sharma",6, 10, 10, 0, 100, null))
//        listPoints.add(ResultViewModel.MultiplayerPointsVm("Mayank Sharma", 5,10, 10, 0, 100, null))
//        listPoints.add(ResultViewModel.MultiplayerPointsVm("Mayank Sharma", 4,10, 10, 0, 100, null))
//        listPoints.add(ResultViewModel.MultiplayerPointsVm("Mayank Sharma", 3,10, 10, 0, 100, null))
//        listPoints.add(ResultViewModel.MultiplayerPointsVm("Mayank Sharma", 2,10, 10, 0, 100, null))
//        listPoints.add(ResultViewModel.MultiplayerPointsVm("Mayank Sharma", 1,10, 10, 0, 100, null))
//        listPoints.add(ResultViewModel.MultiplayerPointsVm("Mayank Sharma", 9,10, 10, 0, 100, null))
//        listPoints.add(ResultViewModel.MultiplayerPointsVm("Mayank Sharma", 8,10, 10, 0, 100, null))
//
//        setRecyclerViewAdapterPoints(listPoints)
//    }
//
//    private fun setItem() {
//        listResult.clear()
//        listResult.add(ResultViewModel.MultiplayerResultVm("Mayank Sharma", 10, null))
//        listResult.add(ResultViewModel.MultiplayerResultVm("Mayank Sharma", 10, null))
//        listResult.add(ResultViewModel.MultiplayerResultVm("Mayank Sharma", 10, null))
//        listResult.add(ResultViewModel.MultiplayerResultVm("Mayank Sharma", 10, null))
//        listResult.add(ResultViewModel.MultiplayerResultVm("Mayank Sharma", 10, null))
//        listResult.add(ResultViewModel.MultiplayerResultVm("Mayank Sharma", 10, null))
//        listResult.add(ResultViewModel.MultiplayerResultVm("Mayank Sharma", 10, null))
//        listResult.add(ResultViewModel.MultiplayerResultVm("Mayank Sharma", 10, null))
//        setRecyclerViewAdapter(listResult)
//
//    }
//
//    private fun setRecyclerViewAdapter(list: List<ResultViewModel.MultiplayerResultVm>) {
//        adapter.items = list
//        adapter.notifyDataSetChanged()
//    }
//
//    private fun setRecyclerViewAdapterPoints(list: List<ResultViewModel.MultiplayerPointsVm>) {
//        adapterPoints.items = list
//        adapterPoints.notifyDataSetChanged()
//    }


}

