package kwizzapp.com.kwizzapp.settings.profilesettings

import android.content.Context
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.mayank.kwizzapp.dependency.components.DaggerInjectFragmentComponent
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_user_info.*
import kwizzapp.com.kwizzapp.KwizzApp

import kwizzapp.com.kwizzapp.R
import kwizzapp.com.kwizzapp.databinding.EditProfileBinding
import kwizzapp.com.kwizzapp.helper.processRequest
import kwizzapp.com.kwizzapp.models.Users

import kwizzapp.com.kwizzapp.services.IUser
import kwizzapp.com.kwizzapp.settings.SettingsActivity
import kwizzapp.com.kwizzapp.viewmodels.UserInfo
import net.rmitsolutions.mfexpert.lms.helpers.*
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.startActivity
import javax.inject.Inject

class EditProfileFragment : Fragment(), View.OnClickListener {

    private var listener: OnFragmentInteractionListener? = null
    @Inject
    lateinit var userService: IUser
    private lateinit var dataBinding: EditProfileBinding
    private lateinit var userInfoVm: UserInfo
    private lateinit var updateData: Button
    private lateinit var compositeDisposable: CompositeDisposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val depComponent = DaggerInjectFragmentComponent.builder()
                .applicationComponent(KwizzApp.applicationComponent)
                .build()
        depComponent.injectEditProfileFragment(this)
        compositeDisposable = CompositeDisposable()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_profile, container, false)
        val view = dataBinding.root
        userInfoVm = UserInfo()
        dataBinding.userInfoVm = userInfoVm
        dataBinding.editTextMobileNumber.keyListener = null
        dataBinding.editTextMobileNumber.setOnClickListener {
            toast("Sorry! Mobile number cannot be change.")
        }
        val firstName = activity?.getPref(SharedPrefKeys.FIRST_NAME, "")
        val lastName = activity?.getPref(SharedPrefKeys.LAST_NAME, "")
        val mobileNumber = activity?.getPref(SharedPrefKeys.MOBILE_NUMBER, "")
        val email = activity?.getPref(SharedPrefKeys.EMAIL, "")
        when {
            firstName != "" || lastName != "" || mobileNumber != "" || email != "" -> {
                dataBinding.userInfoVm!!.firstName.set(firstName)
                dataBinding.userInfoVm!!.lastName.set(lastName)
                dataBinding.userInfoVm?.mobileNumber = mobileNumber
                dataBinding.userInfoVm?.email = email
            }
        }
        updateData = view.find(R.id.buttonUpdateInfo)
        updateData.setOnClickListener(this)
        return view
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.buttonUpdateInfo -> updateInfo()
        }
    }

    private fun updateInfo() {
        if (validate()) {
            val user = Users.UpdateUserInfo()
            user.firstName = dataBinding.userInfoVm?.firstName?.get()
            user.lastName = dataBinding.userInfoVm?.lastName?.get()
            user.email = dataBinding.userInfoVm?.email
            user.mobileNumber = dataBinding.userInfoVm?.mobileNumber
            compositeDisposable.add(userService.updateUserInfo(user)
                    .processRequest(
                            { response ->
                                if (response.isSuccess) {
                                    activity?.putPref(SharedPrefKeys.FIRST_NAME, user.firstName)
                                    activity?.putPref(SharedPrefKeys.LAST_NAME, user.lastName)
                                    activity?.putPref(SharedPrefKeys.EMAIL, user.email)
                                    toast(response.message)
                                    startActivity<SettingsActivity>()
                                    activity?.finish()
                                } else {
                                    toast(response.message)
                                }
                            },
                            { err ->
                                showDialog(activity!!, "Error", err.toString())
                            }
                    ))
        }
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

        return true
    }

    private fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
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