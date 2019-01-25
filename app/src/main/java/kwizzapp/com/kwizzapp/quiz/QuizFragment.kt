package kwizzapp.com.kwizzapp.quiz

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.view.*
import android.widget.ProgressBar
import android.widget.TextSwitcher
import android.widget.TextView
import com.example.mayank.kwizzapp.dependency.components.DaggerInjectFragmentComponent
import com.example.mayank.kwizzapp.libgame.LibPlayGame
import io.reactivex.disposables.CompositeDisposable
import kwizzapp.com.kwizzapp.Constants.DROP_QUESTIONS
import kwizzapp.com.kwizzapp.Constants.RIGHT_ANSWERS
import kwizzapp.com.kwizzapp.Constants.WRONG_ANSWERS
import kwizzapp.com.kwizzapp.KwizzApp


import kwizzapp.com.kwizzapp.R
import kwizzapp.com.kwizzapp.helper.Global
import kwizzapp.com.kwizzapp.helper.processRequest
import kwizzapp.com.kwizzapp.multiplayer.MultiplayerResultFragment
import kwizzapp.com.kwizzapp.services.IQuestion
import kwizzapp.com.kwizzapp.viewmodels.Questions
import net.rmitsolutions.mfexpert.lms.helpers.*
import org.jetbrains.anko.find
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

const val AMOUNT = "Amount"

class QuizFragment : Fragment(), View.OnClickListener {

    private var listener: OnFragmentInteractionListener? = null

    private val SUBJECT_CODE = "SubjectCode"
    private val SUBJECT = "Subject"

    @Inject
    lateinit var questionService: IQuestion

    private var amount: Double? = null
    private var subjectCode: String? = null
    private var subject: String? = null

    lateinit var randomNumbers: ArrayList<Int>
    private var q = 0
    private var answer = ""
    private var rightAnswers = 0
    private var wrongAnswers = 0
    private var dropQuestions = 0
    private lateinit var textSwitcherCountdown: TextSwitcher
    private var countDownTimer: CountDownTimer? = null
    private lateinit var textViewCount: TextView
    private var libPlayGame: LibPlayGame? = null
    private var numberOfRows: Int? = null
    private var progressBar: ProgressBar? = null
    private var timerStatus = TimerStatus.STOPPED
    private var timeCountInMilliSeconds = (1 * 10000).toLong()
    private var textViewSeconds: TextView? = null
    private lateinit var compositeDisposable: CompositeDisposable
    private var show: Boolean = false

    private enum class TimerStatus {
        STARTED,
        STOPPED
    }

    private val CLICKABLES = intArrayOf(R.id.layoutOptionA, R.id.layoutOptionB, R.id.layoutOptionC, R.id.layoutOptionD, R.id.layoutOptionE)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
            amount = it.getDouble(AMOUNT)
            subjectCode = it.getString(SUBJECT_CODE)
            subject = it.getString(SUBJECT)
        }

        logD("Amount - $amount : SubjectCode - $subjectCode : Subject : $subject")
        val depComponent = DaggerInjectFragmentComponent.builder()
                .applicationComponent(KwizzApp.applicationComponent)
                .build()
        depComponent.injectQuizFragment(this)
        libPlayGame = LibPlayGame(activity!!)
        compositeDisposable = CompositeDisposable()

        getRowCountFromTable()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_quiz, container, false)
        progressBar = view.find(R.id.progressBar)
        textViewSeconds = view.find(R.id.textViewSeconds)
        for (id in CLICKABLES) {
            view.find<CardView>(id).setOnClickListener(this)
        }
        return view
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.layoutOptionA -> {
                if (view.find<TextView>(R.id.textViewOptionA).text == answer) {
                    rightAnswers++
                } else {
                    wrongAnswers++
                }

                getQuestionFromServer()

            }

            R.id.layoutOptionB -> {
                if (view.find<TextView>(R.id.textViewOptionB).text == answer) {
                    rightAnswers++
                } else {
                    wrongAnswers++
                }

                getQuestionFromServer()

            }

            R.id.layoutOptionC -> {
                if (view.find<TextView>(R.id.textViewOptionC).text == answer) {
                    rightAnswers++
                } else {
                    wrongAnswers++
                }

                getQuestionFromServer()
            }

            R.id.layoutOptionD -> {
                if (view.find<TextView>(R.id.textViewOptionD).text == answer) {
                    rightAnswers++
                } else {
                    wrongAnswers++
                }

                getQuestionFromServer()
            }

            R.id.layoutOptionE -> {
                if (view.find<TextView>(R.id.textViewOptionE).text == answer) {
                    rightAnswers++
                } else {
                    wrongAnswers++
                }
                getQuestionFromServer()
            }
        }
    }


    private fun getRowCountFromTable() {
        compositeDisposable.add(questionService.getNumberOfRows(subjectCode!!)
                .processRequest(
                        { response ->
                            if (response.isSuccess) {
                                getRandomNonRepeatingIntegers(response.rowCount!!, 1, response.rowCount)
                                getQuestionFromServer()
                            } else {
                                showDialog(activity!!, "Error", response.message)
                                Global.updateCrashlyticsMessage(
                                        activity?.getPref(SharedPrefKeys.MOBILE_NUMBER, "")!!,
                                        "Error while getting no of rows in QuizFragment",
                                        "GetRowCount",
                                        Exception(response.message)
                                )
                            }
                        }, { err ->
                    showDialog(activity!!, "Error", err.toString())
                    Global.updateCrashlyticsMessage(
                            activity?.getPref(SharedPrefKeys.MOBILE_NUMBER, "")!!,
                            "Error while getting no of rows in QuizFragment",
                            "GetRowCount",
                            Exception(err)
                    )
                }))
    }


    private fun getQuestionFromServer() {
        if (Global.checkInternet(activity!!)) {
            showProgress()
            show = false
            if (q < 10) {
                val question = Questions.GetQuestion()
                question.tableName = subjectCode
                question.quesId = randomNumbers[q].toString()
                compositeDisposable.add(questionService.getQuestions(question)
                        .processRequest(
                                { response ->
                                    if (response.isSuccess) {
                                        q++
                                        hideProgress()
                                        setQuestionTextViews(response)
                                        reset()
                                    } else {
                                        hideProgress()
                                        stopCountdown()
                                        showDialog(response.message)
                                        Global.updateCrashlyticsMessage(
                                                activity?.getPref(SharedPrefKeys.MOBILE_NUMBER, "")!!,
                                                "Error while getting Question in QuizFragment",
                                                "GetQuestion",
                                                Exception(response.message)
                                        )

                                    }
                                }, { err ->
                            hideProgress()
                            showDialog(err.toString())
                            Global.updateCrashlyticsMessage(
                                    activity?.getPref(SharedPrefKeys.MOBILE_NUMBER, "")!!,
                                    "Error while getting Question in QuizFragment",
                                    "GetQuestion",
                                    Exception(err)
                            )
                        }
                        ))
            } else {
                logD("Question Finished!")
                hideProgress()
                stopCountdown()
                changeToResultScreen()
            }
        }
    }

    private fun showDialog(message: String) {
        if (!show) {
            activity?.showDialog(activity!!, "Error", message)
            show = true
        }
    }

    private fun stopCountdown() {
        if (countDownTimer != null) {
            countDownTimer?.cancel()
        }
    }

    private fun changeToResultScreen() {
        if (countDownTimer != null) {
            countDownTimer?.cancel()
        }
        libPlayGame?.broadcastResult('R', rightAnswers, wrongAnswers, dropQuestions)
        val bundle = Bundle()
        bundle.putDouble(AMOUNT, amount!!)
        bundle.putInt(RIGHT_ANSWERS, rightAnswers)
        bundle.putInt(WRONG_ANSWERS, wrongAnswers)
        bundle.putInt(DROP_QUESTIONS, dropQuestions)
        val resultFragment = MultiplayerResultFragment()
        resultFragment.arguments = bundle
        switchToFragmentBackStack(resultFragment)

    }

    private fun setQuestionTextViews(response: Questions.Question) {
        hideProgress()
        answer = response.answer!!
        view?.find<TextView>(R.id.text_view_question)?.text = "Q. ${response.question}"
        view?.find<TextView>(R.id.textViewOptionA)?.text = "${response.optionA}"
        view?.find<TextView>(R.id.textViewOptionB)?.text = "${response.optionB}"
        view?.find<TextView>(R.id.textViewOptionC)?.text = "${response.optionC}"
        view?.find<TextView>(R.id.textViewOptionD)?.text = "${response.optionD}"
        view?.find<TextView>(R.id.textViewOptionE)?.text = "${response.optionE}"
    }

    private fun getRandomNonRepeatingIntegers(size: Int, min: Int, max: Int): ArrayList<Int> {
        randomNumbers = ArrayList()

        while (randomNumbers.size < size) {
            val random = getRandomInt(min, max)

            if (!randomNumbers.contains(random)) {
                randomNumbers.add(random)
            }
        }
        return randomNumbers
    }

    private fun getRandomInt(min: Int, max: Int): Int {
        val random = Random()

        return random.nextInt(max - min + 1) + min
    }

    private fun reset() {
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

    private fun stopCountdownTimer() {
        if (timerStatus == TimerStatus.STARTED) {
            countDownTimer?.cancel()
        }
    }

    private fun setTimerValues() {
        var time = -1
        time = if (subjectCode == "aptitude") {
            6
        } else {
            1
        }
        timeCountInMilliSeconds = (time * 10 * 1000).toLong()
    }

    private fun setProgressBarValue() {
        progressBar!!.max = timeCountInMilliSeconds.toInt() / 1000
        progressBar!!.progress = timeCountInMilliSeconds.toInt() * 1000
    }

    private fun startCountdownTimer() {
        countDownTimer = object : CountDownTimer(timeCountInMilliSeconds, 1000) {
            override fun onTick(millisUntilFinished: Long) {

                textViewSeconds!!.text = secondsFormatter(millisUntilFinished)
                progressBar!!.progress = (millisUntilFinished / 1000).toInt()

            }

            override fun onFinish() {
                timerStatus = TimerStatus.STOPPED
                hideProgress()
                if (q < 10) {
                    dropQuestions++
                    getQuestionFromServer()
                } else {
                    dropQuestions++
                    changeToResultScreen()
                    // Broadcast score here
                }
            }

        }.start()
        countDownTimer!!.start()
    }

    private fun secondsFormatter(milliSeconds: Long): String {
        return String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)))
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        activity?.menuInflater?.inflate(R.menu.quiz_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.skip -> {
                if (q < 10) {
                    dropQuestions++
                    getQuestionFromServer()
                } else {
                    dropQuestions++
                    changeToResultScreen()
                }
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
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

    }
}
