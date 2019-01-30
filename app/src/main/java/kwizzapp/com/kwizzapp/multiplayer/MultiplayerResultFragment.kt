package kwizzapp.com.kwizzapp.multiplayer

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.mayank.kwizzapp.dependency.components.DaggerInjectFragmentComponent
import com.example.mayank.kwizzapp.dialog.ProgressDialog
import com.example.mayank.kwizzapp.dialog.ShowDialog
import com.example.mayank.kwizzapp.gameresult.adapter.ResultViewAdapter
import com.example.mayank.kwizzapp.libgame.LibGameConstants
import com.example.mayank.kwizzapp.libgame.LibGameConstants.GameConstants.listResult
import com.example.mayank.kwizzapp.libgame.LibGameConstants.GameConstants.modelList
import com.example.mayank.kwizzapp.libgame.LibGameConstants.GameConstants.resultList
import com.example.mayank.kwizzapp.libgame.LibPlayGame
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.games.Games
import com.google.android.gms.games.multiplayer.Participant
import com.google.firebase.analytics.FirebaseAnalytics
import com.technoholicdeveloper.kwizzapp.achievements.Achievements
import io.reactivex.disposables.CompositeDisposable
import kwizzapp.com.kwizzapp.Constants
import kwizzapp.com.kwizzapp.Constants.DROP_QUESTIONS
import kwizzapp.com.kwizzapp.Constants.RIGHT_ANSWERS
import kwizzapp.com.kwizzapp.Constants.WRONG_ANSWERS
import kwizzapp.com.kwizzapp.KwizzApp
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace


import kwizzapp.com.kwizzapp.R
import kwizzapp.com.kwizzapp.helper.Global
import kwizzapp.com.kwizzapp.helper.processRequest
import kwizzapp.com.kwizzapp.models.Transactions
import kwizzapp.com.kwizzapp.multiplayer.adapter.pointsadapter.PointsViewAdapter
import kwizzapp.com.kwizzapp.quiz.AMOUNT
import kwizzapp.com.kwizzapp.services.ITransaction
import kwizzapp.com.kwizzapp.viewmodels.ResultViewModel
import net.rmitsolutions.mfexpert.lms.helpers.SharedPrefKeys
import net.rmitsolutions.mfexpert.lms.helpers.getPref
import net.rmitsolutions.mfexpert.lms.helpers.logD
import net.rmitsolutions.mfexpert.lms.helpers.showDialog
import org.jetbrains.anko.find
import java.lang.Exception
import java.util.*
import javax.inject.Inject

class MultiplayerResultFragment : Fragment(), View.OnClickListener {

    private var listener: OnFragmentInteractionListener? = null

    @Inject
    lateinit var transactionService: ITransaction
    private lateinit var compositeDisposable: CompositeDisposable

    private var rightAnswers: Int? = 0
    private var wrongAnswers: Int? = 0
    private var dropQuestions: Int? = 0
    private var amount: Double? = 0.0
    private lateinit var resultRecyclerView: RecyclerView
    val adapter: ResultViewAdapter by lazy { ResultViewAdapter() }
    val adapterPoints: PointsViewAdapter by lazy { PointsViewAdapter() }
    private var libPlayGame: LibPlayGame? = null
    private lateinit var buttonBack: Button
    private lateinit var list: MutableList<ResultViewModel.MultiplayerResultVm>
    private lateinit var showDialog: ShowDialog

    private lateinit var showResultProgress: ProgressDialog
    private val syncIntentFilter = IntentFilter(ACTION_RESULT_RECEIVED)
    private lateinit var resultLayout: CardView
    private lateinit var achievements: Achievements
    private var win: Boolean = false
    private var displayName: String? = null
    lateinit var myTrace : Trace
    private lateinit var greetOne : TextView
    private lateinit var greetTwo : TextView
    private lateinit var greetThree : TextView
    private lateinit var resultIcon : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            rightAnswers = it.getInt(RIGHT_ANSWERS)
            wrongAnswers = it.getInt(WRONG_ANSWERS)
            dropQuestions = it.getInt(DROP_QUESTIONS)
            amount = it.getDouble(AMOUNT)
        }
        val depComponent = DaggerInjectFragmentComponent.builder()
                .applicationComponent(KwizzApp.applicationComponent)
                .build()
        depComponent.injectMultiplayerResultFragment(this)
        compositeDisposable = CompositeDisposable()

        myTrace = FirebasePerformance.getInstance().newTrace("multiplayer_result_trace")

        libPlayGame = LibPlayGame(activity!!)
        resultList = mutableListOf<ResultViewModel.MultiplayerResultVm>()
        showResultProgress = ProgressDialog()
        list = mutableListOf<ResultViewModel.MultiplayerResultVm>()

        achievements = Achievements(activity!!)

        showDialog = ShowDialog()

        context?.registerReceiver(resultBroadcastReceiver, syncIntentFilter);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_multiplayer_result, container, false)
        resultRecyclerView = view.findViewById(R.id.result_recycler_view)
        resultRecyclerView.layoutManager = LinearLayoutManager(activity)
        resultRecyclerView.setHasFixedSize(true)
        resultRecyclerView.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
//        buttonBack = view.find(R.id.buttonBack)
        resultLayout = view.find(R.id.result_layout)
        resultLayout.visibility = View.GONE
//        buttonBack.setOnClickListener(this)
        resultRecyclerView.adapter = adapter
        resultIcon = view.find(R.id.result_icon)
        greetOne = view.find(R.id.greetOne)
        greetTwo = view.find(R.id.greetTwo)
        greetThree = view.find(R.id.greetThree)
        showResultProgress.showResultProgress(activity!!)
        setItem()
        return view
    }

    override fun onClick(v: View?) {
        when (v?.id) {
//            R.id.buttonBack -> {
//                myTrace.stop()
//                context?.unregisterReceiver(resultBroadcastReceiver)
//                libPlayGame?.leaveRoom()
//            }
        }
    }

    private fun setItem() {
        modelList.clear()
        displayName = activity?.getPref(SharedPrefKeys.DISPLAY_NAME, "")
        modelList.add(ResultViewModel.MultiplayerResultVm(displayName!!, rightAnswers!!, LibGameConstants.GameConstants.imageUri, wrongAnswers!!, dropQuestions!!))

        for (p in LibGameConstants.GameConstants.mParticipants!!) {
            if (LibGameConstants.GameConstants.mRoomId != null) {
                val pid = p.participantId
                if (pid == LibGameConstants.GameConstants.mMyId) {
                    LibGameConstants.GameConstants.mFinishedParticipants.add(pid)
                }
            }
        }
        updateScore()
    }

    private val resultBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (MultiplayerResultFragment.ACTION_RESULT_RECEIVED == intent.action) {
                updateScore()
            }
        }
    }

    private var show: Boolean = false

    private fun updateScore() {
        if (LibGameConstants.GameConstants.mRoomId != null) {
            for (p in LibGameConstants.GameConstants.mParticipants!!) {
                val pid = p.participantId
                if (pid == LibGameConstants.GameConstants.mMyId) {
                    continue
                }
                if (p.status != Participant.STATUS_JOINED) {
                    continue
                }

                if (LibGameConstants.GameConstants.mParticipants?.size != LibGameConstants.GameConstants.mFinishedParticipants.size) {
                    continue
                }

                val score = if (LibGameConstants.GameConstants.mParticipantScore.containsKey(pid)) LibGameConstants.GameConstants.mParticipantScore[pid] else 0

                val wrong = if (LibGameConstants.GameConstants.mParticipantWrong.containsKey(pid)) LibGameConstants.GameConstants.mParticipantWrong[pid] else 0
                val drop = if (LibGameConstants.GameConstants.mParticipantDrop.containsKey(pid)) LibGameConstants.GameConstants.mParticipantDrop[pid] else 0

                if (LibGameConstants.GameConstants.mParticipants?.size == LibGameConstants.GameConstants.mFinishedParticipants.size) {
                    if (p.displayName != displayName) {
                        if (!listResult.containsKey(p.displayName)) {
                            listResult[p.displayName] = ResultViewModel.MultiplayerResultVm(p.displayName, score!!, p.iconImageUri, wrong!!, drop!!)
                            val resultViewModel = listResult[p.displayName]
                            modelList.add(resultViewModel!!)
                        }
                        // If model list is equals finishedParticipants size then arrange list in descending order
                        if (modelList.size == LibGameConstants.GameConstants.mFinishedParticipants.size) {
                            resultList = modelList.sortedByDescending {
                                it.rightAnswers
                            }.toMutableList()
                            showResultDialog(resultList)
                            showResultProgress.hideProgressDialog()
                            //buttonBack.visibility = View.VISIBLE
                        }

                    } else {
                        logD("Display name are same")
                    }
                } else {
                }
            }
            setRecyclerViewAdapter(resultList!!)
            // Check this where to write coz of this also will be a problem
        }
    }

    private fun showResultDialog(resultList: MutableList<ResultViewModel.MultiplayerResultVm>?) {
        myTrace.start()
        if (resultList!![0].playerName == displayName) {
            logD("List 0 score - ${resultList[0].rightAnswers} List 1 score - ${resultList[1].rightAnswers}")
            val updateResult = Transactions.ResultBalance()
            if (resultList[0].rightAnswers == resultList[1].rightAnswers) {
                if (!show) {
                    win = false
                    //showDialogResult(activity!!, "Sorry", "It's a Tie", "Your bid points will credited to your wallet", R.mipmap.ic_loose)
                    updateResult.displayName = displayName
                    updateResult.amount = amount
                    updateResult.timeStamp = Global.getFormatDate(Calendar.getInstance().time)
                    updateResult.productInfo = "Points refund due to tie in Quiz."
                    updateBalance(updateResult)
                    show = true
                    resultLayout.visibility = View.VISIBLE
                    myTrace.incrementMetric("tie_game", 1)
                }
            } else {
                if (!show) {
                    win = true
                    //showDialogResult(activity!!, "Congrats", "You Win !", "Your winning points will credited to your wallet", R.mipmap.ic_done)
                    val totalAmount = (amount?.times(LibGameConstants.GameConstants.mFinishedParticipants.size))?.times(80)?.div(100)
                    updateResult.displayName = displayName
                    updateResult.amount = totalAmount
                    updateResult.timeStamp = Global.getFormatDate(Calendar.getInstance().time)
                    updateResult.productInfo = "You have won for Quiz."
                    updateBalance(updateResult)
                    show = true
                    resultLayout.visibility = View.VISIBLE
                    myTrace.incrementMetric("win_game", 1)
                }
            }
        } else {
            if (!show) {
                win = false
                //showDialogResult(activity!!, "Sorry", "You Loose !", "Better luck next time", R.mipmap.ic_loose)
                show = true
                resultLayout.visibility = View.VISIBLE
                greetThree.textSize = 14F
                greetThree.typeface = Typeface.DEFAULT
                setGreetingText("Sorry", "You Loose", "Better luck next time", R.mipmap.ic_result_loose)
                achievements.checkAchievements(0, LibGameConstants.GameConstants.resultList?.size!!, win, rightAnswers!!)
                myTrace.incrementMetric("loose_game", 1)
            }
        }
    }

    private fun submitScoreToLeaderboards(score: Long) {
        Games.getLeaderboardsClient(activity!!, GoogleSignIn.getLastSignedInAccount(activity)!!)
                .submitScore(getString(R.string.leaderboard_toppers), score * 1000000);
        val bundle = Bundle()
        bundle.putLong(FirebaseAnalytics.Param.SCORE, score);
        bundle.putString("leaderboard_id", getString(R.string.leaderboard_toppers));
        Constants.firebaseAnalytics.logEvent(FirebaseAnalytics.Event.POST_SCORE, bundle);
    }

    private fun showDialogResult(activity: Activity, bigTitle: String, smallTitle: String, message: String, imageResource: Int) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            showDialog.showResultDialog(activity, bigTitle, smallTitle, message, imageResource)
        } else {
            showDialog(activity, bigTitle, "$smallTitle\n\n$message")
        }
    }


    private fun updateBalance(updateResult: Transactions.ResultBalance) {
        compositeDisposable.add(transactionService.updateResultBalance(updateResult)
                .processRequest(
                        { response ->
                            if (response.isSuccess) {
                                val bundle = Bundle()
                                bundle.putDouble("Amount", updateResult.amount!!)
                                bundle.putString("DisplayName", updateResult.displayName)
                                bundle.putString("Name", "${updateResult.timeStamp} ${updateResult.productInfo}")
                                Constants.firebaseAnalytics.logEvent("MultiplayerResult", bundle)
                                submitScoreToLeaderboards(response.balance.toLong())
                                achievements.checkAchievements(response.balance.toInt(), resultList?.size!!, win, rightAnswers!!)
                                if (updateResult.amount.toString().toDouble() > amount!!){
                                    setGreetingText("Congratulations", "You've won", "${updateResult.amount}", R.mipmap.ic_result_win)
                                }else{
                                    greetThree.textSize = 14F
                                    greetThree.typeface = Typeface.DEFAULT
                                    setGreetingText("Sorry", "It's a tie", "Try one more time", R.mipmap.ic_result_loose)
                                }
                            } else {
                                showDialog(activity!!, "Error", response.message)
                                Global.updateCrashlyticsMessage(updateResult.displayName!!, "Error while updating result points on multiplayer result fragment.", "UpdatingPoints", Exception(response.message))
                            }
                        },
                        { error ->
                            showDialog(activity!!, "Error", error.toString())
                            Global.updateCrashlyticsMessage(updateResult.displayName!!, "Error while updating result points on multiplayer result fragment.", "UpdatingPoints", Exception(error))
                        }
                ))
    }

    private fun setGreetingText(greet1 : String, greet2 : String, greet3 : String, resource :Int){
        greetOne.text = greet1
        greetTwo.text = greet2
        greetThree.text = greet3
        resultIcon.setImageResource(resource)
    }

    private fun updateGameStatus() {

    }

    private fun setRecyclerViewAdapter(list: List<ResultViewModel.MultiplayerResultVm>) {
        adapter.items = list
        adapter.notifyDataSetChanged()
    }

    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        const val ACTION_RESULT_RECEIVED = "com.technoholicdeveloper.kwizzapp.ACTION_RESULT_RECEIVED"
    }
}
