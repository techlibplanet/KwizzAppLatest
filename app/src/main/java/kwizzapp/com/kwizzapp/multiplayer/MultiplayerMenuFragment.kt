package kwizzapp.com.kwizzapp.multiplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.mayank.kwizzapp.dependency.components.DaggerInjectFragmentComponent
import com.example.mayank.kwizzapp.libgame.LibPlayGame
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.game_detail_layout.*
import kwizzapp.com.kwizzapp.Constants
import kwizzapp.com.kwizzapp.Constants.firebaseAnalytics
import kwizzapp.com.kwizzapp.KwizzApp

import kwizzapp.com.kwizzapp.R
import kwizzapp.com.kwizzapp.helper.Global
import kwizzapp.com.kwizzapp.helper.processRequest
import kwizzapp.com.kwizzapp.models.Transactions
import kwizzapp.com.kwizzapp.quiz.QuizFragment
import kwizzapp.com.kwizzapp.services.ITransaction
import net.rmitsolutions.mfexpert.lms.helpers.*
import org.jetbrains.anko.find
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MultiplayerMenuFragment : Fragment(), View.OnClickListener {
    private var listener: OnFragmentInteractionListener? = null

    @Inject
    lateinit var transactionService: ITransaction
    private lateinit var libPlayGame: LibPlayGame
    private var i = -1
    private var k = -1
    private var subject: String? = null
    private var subCode: String? = null
    private var amount: String? = null
    private lateinit var amountList: Array<String>
    private lateinit var subjectList: Array<String>
    private lateinit var subjectCode: Array<String>
    private val syncIntentFilter = IntentFilter(ACTION_MESSAGE_RECEIVED)
    private var timerStatus = TimerStatus.STOPPED
    private var progressBar: ProgressBar? = null
    private var textViewSeconds: TextView? = null
    private var countDownTimer: CountDownTimer? = null
    private var timeCountInMilliSeconds = (1 * 10000).toLong()
    private val CLICKABLES = intArrayOf(R.id.imageButtonNextAmount, R.id.imageButtonNextSubject,
            R.id.imageButtonPreviousAmount, R.id.imageButtonPreviousSubject)
    private var textLabel: TextView? = null
    private var subtract: Boolean = false
    private lateinit var compositeDisposable: CompositeDisposable

    private enum class TimerStatus {
        STARTED,
        STOPPED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        val depComponent = DaggerInjectFragmentComponent.builder()
                .applicationComponent(KwizzApp.applicationComponent)
                .build()
        depComponent.injectMultiplayerMenuFragment(this)
        compositeDisposable = CompositeDisposable()
        libPlayGame = LibPlayGame(activity!!)
        subjectList = resources.getStringArray(R.array.subjectList)
        subjectCode = resources.getStringArray(R.array.subject_code)
        amountList = resources.getStringArray(R.array.amount)
        activity?.registerReceiver(messageBroadcastReceiver, syncIntentFilter)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_multiplayer_menu, container, false)
        progressBar = view.find(R.id.progressBar)
        textViewSeconds = view.find(R.id.textViewSeconds)
        textLabel = view.find(R.id.textViewLabel)
        view.find<Button>(R.id.buttonLeaveRoom).setOnClickListener(this)
        for (id in CLICKABLES) {
            view.findViewById<ImageView>(id).setOnClickListener(this)
        }
        return view
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imageButtonNextAmount -> {
                nextAmount(k)
            }

            R.id.imageButtonPreviousAmount -> {
                previousAmount(k)
            }
            R.id.imageButtonPreviousSubject -> {
                previousSubject(i)
            }

            R.id.imageButtonNextSubject -> {
                nextSubject(i)
            }

            R.id.buttonLeaveRoom -> {
                libPlayGame.leaveRoom()
            }
        }
    }


    private val messageBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (ACTION_MESSAGE_RECEIVED == intent.action) {
                val state = intent.getCharExtra("state", 'Z')
                val value = intent.getIntExtra("value", -1)
                if (state == 'A') {
                    k = value
                    setTextAmount(k)
                    reset()
                } else if (state == 'S') {
                    i = value
                    setTextSubject(i)
                    reset()
                }
            }
        }
    }

    private fun setTextAmount(value: Int) {
        amount = amountList[value]
        textViewAmount.text = amount
    }

    private fun setTextSubject(value: Int) {
        subCode = subjectCode[value]
        subject = subjectList[value]
        textViewSubject.text = subject
    }

    private fun nextAmount(value: Int) {
        k = value
        if (k < 20) {
            k++
            amount = amountList[k]
            textViewAmount.text = amount
        } else {
            k = 0
            amount = amountList[k]
            textViewAmount.text = amount
        }
        libPlayGame.broadcastMessage('A', k)
        reset()
    }

    private fun previousAmount(value: Int) {
        k = value
        if (k > 0) {
            k--
            amount = amountList[k]
            textViewAmount.text = amount
        } else {
            this.k = 20
            amount = amountList[this.k]
            textViewAmount.text = amount
        }
        libPlayGame.broadcastMessage('A', k)
        reset()
    }

    private fun nextSubject(value: Int) {
        i = value
        if (i < 6) {
            i++
            subject = subjectList[i]
            subCode = subjectCode[i]
            textViewSubject.text = subject
        } else {
            i = 0
            subject = subjectList[i]
            subCode = subjectCode[i]
            textViewSubject.text = subject
        }
        libPlayGame.broadcastMessage('S', i)
        reset()
    }

    private fun previousSubject(value: Int) {
        i = value
        if (i > 0) {
            i--
            subCode = subjectCode[i]
            subject = subjectList[i]
            textViewSubject.text = subject
        } else {
            i = 6
            subCode = subjectCode[i]
            subject = subjectList[i]
            textViewSubject.text = subject
        }
        libPlayGame.broadcastMessage('S', i)
        reset()
    }

    private fun unRegisterBroadcastReceiver() {
        try {
            if (messageBroadcastReceiver != null) {
                activity?.unregisterReceiver(messageBroadcastReceiver)
            }
        } catch (e: IllegalArgumentException) {
            logE("Error - $e")
        }
    }

    private fun reset() {
        textLabel?.visibility = View.VISIBLE
        if (timerStatus == TimerStatus.STOPPED) {
            setTimerValues()
            setProgressBarValue()
            timerStatus = TimerStatus.STARTED
            startCountdownTimer()
            return
        }
        stopCountdownTimer()
        startCountdownTimer()
    }

    private fun setTimerValues() {
        var time = 1
        // assigning values after converting to milliseconds
        timeCountInMilliSeconds = (time * 10 * 1000).toLong()
    }

    private fun startCountdownTimer() {
        countDownTimer = object : CountDownTimer(timeCountInMilliSeconds, 1000) {
            override fun onTick(millisUntilFinished: Long) {

                textViewSeconds!!.text = secondsFormatter(millisUntilFinished)
                progressBar!!.progress = (millisUntilFinished / 1000).toInt()

            }

            override fun onFinish() {
                timerStatus = TimerStatus.STOPPED

                if (amount == null) {
                    Toast.makeText(activity, "Select a valid Amount!", Toast.LENGTH_SHORT).show()
                } else if (subject == null) {
                    Toast.makeText(activity, "Select a valid subject!", Toast.LENGTH_SHORT).show()
                } else {
                    if (!subtract) {
                        subtract = true
                        val subtractBalance = Transactions.SubtractBalance()
                        subtractBalance.firstName = activity?.getPref(SharedPrefKeys.FIRST_NAME, "")
                        subtractBalance.lastName = activity?.getPref(SharedPrefKeys.LAST_NAME, "")
                        subtractBalance.mobileNumber = activity?.getPref(SharedPrefKeys.MOBILE_NUMBER, "")
                        subtractBalance.email = activity?.getPref(SharedPrefKeys.EMAIL, "")
                        subtractBalance.amount = amount?.toDouble()
                        subtractBalance.playerId = activity?.getPref(SharedPrefKeys.PLAYER_ID, "")
                        subtractBalance.productInfo = "Debited for play Quiz"
                        subtractBalance.addedOn = System.currentTimeMillis().toString()
                        subtractBalance.createdOn = System.currentTimeMillis().toString()
                        subtractBalance.transactionType = "Debited"
                        subtractBalance.status = "Success"
                        subtractBalance(subtractBalance)
                    }
                }
            }

        }.start()
        countDownTimer!!.start()
    }

    private fun subtractBalance(subtractBalance: Transactions.SubtractBalance) {
        if (Global.checkInternet(activity!!)) {
            compositeDisposable.add(transactionService.subtractBalance(subtractBalance)
                    .processRequest(
                            { response ->
                                if (response.isSuccess) {
                                    if (countDownTimer != null) {
                                        countDownTimer?.cancel()
                                    }
                                    val bundle = Bundle()
                                    bundle.putString("Subject", subject)
                                    bundle.putString("SubjectCode", subCode)
                                    bundle.putDouble("Amount", subtractBalance.amount!!)
                                    bundle.putString("MobileNumber", activity?.getPref(SharedPrefKeys.MOBILE_NUMBER, ""))
                                    firebaseAnalytics.logEvent("SelectedMultiplayerOptions", bundle)
                                    val quizFragment = QuizFragment()
                                    quizFragment.arguments = bundle
                                    switchToFragmentBackStack(quizFragment)
                                    unRegisterBroadcastReceiver()
                                } else {
                                    subtract = false
                                    showDialog(activity!!, "Error", response.message)
                                    Global.updateCrashlyticsMessage(subtractBalance.mobileNumber!!, "Error while subtracting points on Game Menu Fragment", "SubtractPoints", Exception(response.message))
                                }

                            }, { err ->
                        subtract = false
                        showDialog(activity!!, "Error", err.toString())
                        Global.updateCrashlyticsMessage(subtractBalance.mobileNumber!!, "Error while subtracting points on Game Menu Fragment", "SubtractPoints", Exception(err))
                    }))
        }
    }

    private fun stopCountdownTimer() {
        if (timerStatus == TimerStatus.STARTED) {
            countDownTimer?.cancel()
        }
    }

    private fun setProgressBarValue() {
        progressBar!!.max = timeCountInMilliSeconds.toInt() / 1000
        progressBar!!.progress = timeCountInMilliSeconds.toInt() * 1000
    }

    private fun secondsFormatter(milliSeconds: Long): String {
        return String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)))
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar?.show()
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
        const val ACTION_MESSAGE_RECEIVED = "com.technoholicdeveloper.kwizzapp.ACTION_MESSAGE_RECEIVED"
    }

}
