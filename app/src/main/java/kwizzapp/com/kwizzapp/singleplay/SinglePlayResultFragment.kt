package kwizzapp.com.kwizzapp.singleplay

import android.content.Context
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.technoholicdeveloper.kwizzapp.achievements.Achievements
import kwizzapp.com.kwizzapp.Constants
import kwizzapp.com.kwizzapp.MainActivity

import kwizzapp.com.kwizzapp.R
import kwizzapp.com.kwizzapp.databinding.SinglePlayResultBinding
import kwizzapp.com.kwizzapp.viewmodels.SinglePlayResultVm
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.startActivity

class SinglePlayResultFragment : Fragment() {
    private var listener: OnFragmentInteractionListener? = null
    private var rightAnswers: Int? = 0
    private var wrongAnswers: Int? = 0
    private var dropQuestions: Int? = 0
    private var noOfQues: Int? = 0
    private lateinit var dataBinding: SinglePlayResultBinding
    private lateinit var singlePlayResultVm: SinglePlayResultVm
    private lateinit var achievements: Achievements

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            rightAnswers = it.getInt(Constants.RIGHT_ANSWERS)
            wrongAnswers = it.getInt(Constants.WRONG_ANSWERS)
            dropQuestions = it.getInt(Constants.DROP_QUESTIONS)
            noOfQues = it.getInt(NO_OF_QUES)
        }
        achievements = Achievements(activity!!)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_single_play_result, container, false)
        val view = dataBinding.root
        singlePlayResultVm = SinglePlayResultVm("", "", "", "")
        val resultImage = view.find<ImageView>(R.id.quiz_result_image)
        if (rightAnswers!! > wrongAnswers!! && rightAnswers!! > dropQuestions!!) {
            resultImage.setImageResource(R.mipmap.result_thumbs_up)
        } else {
            resultImage.setImageResource(R.mipmap.result_thumbs_down)
        }
        dataBinding.singlePlayResultVm = singlePlayResultVm
        dataBinding.singlePlayResultVm?.totalQues = noOfQues.toString()
        dataBinding.singlePlayResultVm?.trueQues = rightAnswers.toString()
        dataBinding.singlePlayResultVm?.falseQues = wrongAnswers.toString()
        dataBinding.singlePlayResultVm?.dropQues = dropQuestions.toString()

        achievements.checkSinglePlayerAchievements(rightAnswers!!)

        view.find<Button>(R.id.buttonSinglePlayBack).setOnClickListener{
            startActivity<MainActivity>()
            activity?.finish()
        }
        return view
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

}