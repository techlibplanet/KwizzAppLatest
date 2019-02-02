package kwizzapp.com.kwizzapp.settings.bankdetails

import android.content.Context
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import kwizzapp.com.kwizzapp.R
import kwizzapp.com.kwizzapp.databinding.EditBankDetailsBinding
import kwizzapp.com.kwizzapp.settings.SettingsActivity
import kwizzapp.com.kwizzapp.viewmodels.UserBankDetails
import net.rmitsolutions.mfexpert.lms.helpers.SharedPrefKeys
import net.rmitsolutions.mfexpert.lms.helpers.getPref
import net.rmitsolutions.mfexpert.lms.helpers.putPref
import net.rmitsolutions.mfexpert.lms.helpers.toast
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.startActivity

class EditBankDetailsFragment : Fragment(), View.OnClickListener {

    private var listener: OnFragmentInteractionListener? = null
    private lateinit var dataBinding: EditBankDetailsBinding
    private lateinit var userInfoVm: UserBankDetails
    private lateinit var updateData: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_bank_details, container, false)
        val view = dataBinding.root
        userInfoVm = UserBankDetails()
        dataBinding.userBankDetailsVm = userInfoVm
        val firstName = activity?.getPref(SharedPrefKeys.FIRST_NAME, "")
        val lastName = activity?.getPref(SharedPrefKeys.LAST_NAME, "")
        val accountNumber = activity?.getPref(SharedPrefKeys.ACCOUNT_NUMBER, "")
        val ifscCode = activity?.getPref(SharedPrefKeys.IFSC_CODE, "")
        when {
            firstName != "" || lastName != "" || accountNumber != "" || ifscCode != "" -> {
                dataBinding.userBankDetailsVm!!.firstName = firstName
                dataBinding.userBankDetailsVm!!.lastName = lastName
                dataBinding.userBankDetailsVm?.accountNumber = accountNumber
                dataBinding.userBankDetailsVm?.ifscCode = ifscCode
            }
        }

        updateData = view.find(R.id.buttonUpdateBankDetails)
        updateData.setOnClickListener(this)
        return view
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.buttonUpdateBankDetails -> updateBankDetails()
        }
    }

    private fun updateBankDetails() {
        activity?.putPref(SharedPrefKeys.FIRST_NAME, dataBinding.userBankDetailsVm?.firstName)
        activity?.putPref(SharedPrefKeys.LAST_NAME, dataBinding.userBankDetailsVm?.lastName)
        activity?.putPref(SharedPrefKeys.ACCOUNT_NUMBER, dataBinding.userBankDetailsVm?.accountNumber)
        activity?.putPref(SharedPrefKeys.IFSC_CODE, dataBinding.userBankDetailsVm?.ifscCode)
        toast("Account details updated successfully !")
        startActivity<SettingsActivity>()
        activity?.finish()
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
