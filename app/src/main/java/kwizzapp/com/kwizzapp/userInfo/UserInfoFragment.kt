package kwizzapp.com.kwizzapp.userInfo

import android.content.Context
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.mayank.kwizzapp.dependency.components.DaggerInjectFragmentComponent
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.iid.FirebaseInstanceId
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_user_info.*
import kwizzapp.com.kwizzapp.Constants.firebaseAnalytics
import kwizzapp.com.kwizzapp.KwizzApp
import kwizzapp.com.kwizzapp.R
import kwizzapp.com.kwizzapp.dashboard.DashboardFragment
import kwizzapp.com.kwizzapp.databinding.UserInfoBinding
import kwizzapp.com.kwizzapp.helper.Global
import kwizzapp.com.kwizzapp.helper.processRequest
import kwizzapp.com.kwizzapp.models.Users
import kwizzapp.com.kwizzapp.services.IUser
import kwizzapp.com.kwizzapp.viewmodels.UserInfo
import net.rmitsolutions.mfexpert.lms.helpers.*
import org.jetbrains.anko.find
import java.lang.Exception
import javax.inject.Inject


class UserInfoFragment : Fragment(), View.OnClickListener {

    @Inject
    lateinit var userService: IUser
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var dataBinding: UserInfoBinding
    private lateinit var userInfoVm: UserInfo
    private lateinit var submitData: Button
    private lateinit var compositeDisposable: CompositeDisposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        compositeDisposable = CompositeDisposable()
        val depComponent = DaggerInjectFragmentComponent.builder()
                .applicationComponent(KwizzApp.applicationComponent)
                .build()
        depComponent.injectUserInfoFragment(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_info, container, false)
        val view = dataBinding.root
        userInfoVm = UserInfo()
        dataBinding.userInfoVm = userInfoVm
        val firstName = activity?.getPref(SharedPrefKeys.FIRST_NAME, "")
        val lastName = activity?.getPref(SharedPrefKeys.LAST_NAME, "")
        when {
            firstName != "" || lastName != "" -> {
                dataBinding.userInfoVm!!.firstName.set(firstName)
                dataBinding.userInfoVm!!.lastName.set(lastName)
            }
        }
        submitData = view.find(R.id.buttonSubmitInfo)
        submitData.setOnClickListener(this)
        return view
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.buttonSubmitInfo -> {
                val userInfo = Users.InsertUserInfo()
                userInfo.firstName = dataBinding.userInfoVm?.firstName?.get()
                userInfo.lastName = dataBinding.userInfoVm?.lastName?.get()
                userInfo.mobileNumber = dataBinding.userInfoVm?.mobileNumber
                userInfo.playerId = activity?.getPref(SharedPrefKeys.PLAYER_ID, "")
                userInfo.email = dataBinding.userInfoVm?.email
                userInfo.displayName = activity?.getPref(SharedPrefKeys.DISPLAY_NAME, "")
                userInfo.fcmTokenId  = activity?.getPref(SharedPrefKeys.FCM_TOKEN_ID, "")
                userInfo.firebaseInstanceId = FirebaseInstanceId.getInstance().id
                dataBinding.userInfoVm?.playerId = activity?.getPref(SharedPrefKeys.PLAYER_ID, "")
                if (validate()) {
                    showProgress()
                    compositeDisposable.add(userService.insertUserInfo(userInfo)
                            .processRequest(
                                    { response ->
                                        hideProgress()
                                        if (response.isSuccess) {
                                            addFireBase(userInfo.mobileNumber!!)
                                            toast(response.message)
                                            switchToDashboard(dataBinding.userInfoVm!!)
                                        } else {
                                            toast(response.message)
                                        }
                                    },
                                    { err ->
                                        hideProgress()
                                        showDialog(activity!!, "Error", err.toString())
                                        logD("Error - ${err.toString()}")
                                        Global.updateCrashlyticsMessage(userInfo.playerId!!, "Error on posting user info to server", "Post User Info", Exception(err))
                                    }
                            ))
                }
            }
        }
    }

    private fun addFireBase(mobileNumber: String) {
        val bundle = Bundle()
        bundle.putString("mobileNumber", mobileNumber)
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
    }

    private fun validate(): Boolean {
        when {
            dataBinding.userInfoVm?.firstName?.get().isNullOrBlank() -> {
                textInputLayoutFirstName.error = "Enter First Name."
                return false
            }
            else -> textInputLayoutFirstName.error = null
        }

        when {
            dataBinding.userInfoVm?.lastName?.get().isNullOrBlank() -> {
                textInputLayoutLastName.error = "Enter Last Name."
                return false
            }
            else -> textInputLayoutLastName.error = null
        }
        when {
            dataBinding.userInfoVm?.mobileNumber.isNullOrBlank() -> {
                textInputLayoutMobileNumber.error = "Enter Mobile Number."
                return false
            }
            else -> textInputLayoutMobileNumber.error = null
        }
        when {
            dataBinding.userInfoVm?.email.isNullOrBlank() -> {
                textInputLayoutEmail.error = "Enter Email"
                return false
            }
            else -> textInputLayoutEmail.error = null
        }

        when {
            !isValidEmail(dataBinding.userInfoVm?.email!!) -> {
                textInputLayoutEmail.error = "Enter valid Email Address"
                return false
            }
            else -> textInputLayoutEmail.error = null
        }

        if (Global.isValidPhone(dataBinding.userInfoVm?.mobileNumber.toString())) {
            textInputLayoutMobileNumber.isErrorEnabled = false
        } else {
            textInputLayoutMobileNumber.error = "Please Enter Valid 10 digit Mobile Number."
            return false
        }

        if (!Global.checkInternet(activity!!)){
            return false
        }

        return true
    }

    private fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    private fun switchToDashboard(userInfoVm: UserInfo) {
        activity?.putPref(SharedPrefKeys.FIRST_NAME, userInfoVm.firstName.get())
        activity?.putPref(SharedPrefKeys.LAST_NAME, userInfoVm.lastName.get())
        activity?.putPref(SharedPrefKeys.MOBILE_NUMBER, userInfoVm.mobileNumber)
        activity?.putPref(SharedPrefKeys.EMAIL, userInfoVm.email)
        val dashboardFrag = DashboardFragment()
        switchToFragment(dashboardFrag)
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
        when (context) {
            is OnFragmentInteractionListener -> listener = context
            else -> throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
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
