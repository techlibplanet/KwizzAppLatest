package kwizzapp.com.kwizzapp.singleplay

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import kotlinx.android.synthetic.main.single_player_menu_layout.*

import kwizzapp.com.kwizzapp.R
import kwizzapp.com.kwizzapp.helper.Global
import net.rmitsolutions.mfexpert.lms.helpers.switchToFragment
import net.rmitsolutions.mfexpert.lms.helpers.toast
import org.jetbrains.anko.find

class SinglePlayMenuFragment : Fragment(), View.OnClickListener {

    private var listener: OnFragmentInteractionListener? = null
    private var i = -1
    private var j: Int = 0
    private var k = -1
    private var l: Int = 0
    private var subject: String? = null
    private var subCode: String? = null
    private var noOfQues: String? = null
    private lateinit var noOfQuesList: Array<String>
    private lateinit var subjectList: Array<String>
    private lateinit var subjectCode: Array<String>
    private val CLICKABLES = intArrayOf(R.id.imageButtonNextQues, R.id.imageButtonNextSubject,
            R.id.imageButtonPreviousQues, R.id.imageButtonPreviousSubject)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        subjectList = resources.getStringArray(R.array.subjectList)
        subjectCode = resources.getStringArray(R.array.subject_code)
        noOfQuesList = resources.getStringArray(R.array.no_of_ques)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_single_play_menu, container, false)
        view.find<Button>(R.id.buttonStart).setOnClickListener(this)
        for (id in CLICKABLES) {
            view.findViewById<ImageView>(id).setOnClickListener(this)
        }
        return view
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.buttonStart -> startSinglePlayGame()
            R.id.imageButtonNextQues -> nextQuestion()
            R.id.imageButtonPreviousQues -> previousQuestion()
            R.id.imageButtonNextSubject -> nextSubject()
            R.id.imageButtonPreviousSubject -> previousSubject()
        }
    }

    private fun previousSubject() {
        if (j > 0) {
            j--
            i = j
            subCode = subjectCode[j]
            subject = subjectList[j]
            textViewSubjectSinglePlay.text = subject
        } else {
            j = 6
            subCode = subjectCode[j]
            subject = subjectList[j]
            textViewSubjectSinglePlay.text = subject
        }
    }

    private fun nextSubject() {
        if (i < 6) {
            i++
            j = i
            subject = subjectList[i]
            subCode = subjectCode[i]
            textViewSubjectSinglePlay.text = subject
        } else {
            i = 0
            subject = subjectList[i]
            subCode = subjectCode[i]
            textViewSubjectSinglePlay.text = subject
        }
    }

    private fun previousQuestion() {
        if (l > 0) {
            l--
            k = l
            noOfQues = noOfQuesList[l]
            textViewNoOfQues.text = noOfQues
        } else {
            l = 1
            noOfQues = noOfQuesList[l]
            textViewNoOfQues.text = noOfQues
        }
    }

    private fun nextQuestion() {
        if (k < 1) {
            k++
            l = k
            noOfQues = noOfQuesList[k]
            textViewNoOfQues.text = noOfQues
        } else {
            k = 0
            noOfQues = noOfQuesList[k]
            textViewNoOfQues.text = noOfQues
        }
    }

    private fun startSinglePlayGame() {
        if (validate()){
            if (Global.checkInternet(activity!!)){
                val bundle = Bundle()
                bundle.putString("Subject", subject)
                bundle.putString("SubjectCode", subCode)
                bundle.putString("NoOfQues", noOfQues)
                val singlePlayQuizFragment = SinglePlayQuizFragment()
                singlePlayQuizFragment.arguments = bundle
                switchToFragment(singlePlayQuizFragment)
            }
        }
    }
    private fun validate(): Boolean {
        if (noOfQues == null){
            toast("Select number of Questions")
            return false
        }
        if (subject == null){
            toast("Select subject")
            return false
        }
        return true
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
