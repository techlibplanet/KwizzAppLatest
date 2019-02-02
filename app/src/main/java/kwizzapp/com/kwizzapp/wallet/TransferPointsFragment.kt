package com.example.mayank.kwizzapp.wallet

import android.content.Context
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.mayank.kwizzapp.dependency.components.DaggerInjectFragmentComponent
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.transfer_points_layout.*
import kwizzapp.com.kwizzapp.Constants
import kwizzapp.com.kwizzapp.Constants.firebaseAnalytics
import kwizzapp.com.kwizzapp.KwizzApp
import kwizzapp.com.kwizzapp.R
import kwizzapp.com.kwizzapp.databinding.TransferPointsBinding
import kwizzapp.com.kwizzapp.helper.Global
import kwizzapp.com.kwizzapp.helper.processRequest
import kwizzapp.com.kwizzapp.models.Transactions
import kwizzapp.com.kwizzapp.services.ITransaction
import kwizzapp.com.kwizzapp.viewmodels.TransferPoints
import kwizzapp.com.kwizzapp.wallet.WalletActivity
import net.rmitsolutions.mfexpert.lms.helpers.*
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.startActivity
import java.lang.Exception
import java.util.*
import javax.inject.Inject


class TransferPointsFragment : Fragment(), View.OnClickListener {

    private var listener: OnFragmentInteractionListener? = null
    @Inject
    lateinit var transactionService: ITransaction
    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var dataBinding: TransferPointsBinding
    private lateinit var transferPoints: TransferPoints
    private lateinit var myTrace : Trace

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        val depComponent = DaggerInjectFragmentComponent.builder()
                .applicationComponent(KwizzApp.applicationComponent)
                .build()
        depComponent.injectTransferPointsFragment(this)
        compositeDisposable = CompositeDisposable()

        myTrace = FirebasePerformance.getInstance().newTrace("transfer_points_trace")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_transfer_points, container, false)
        val view = dataBinding.root
        transferPoints = TransferPoints()
        dataBinding.transferPointsVm = transferPoints
        view.find<Button>(R.id.buttonTransferPoints).setOnClickListener(this)
        return view
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.buttonTransferPoints -> transferPoint()
        }
    }

    private fun transferPoint() {
        myTrace.start()
        val transfer = Transactions.TransferPointsToServer()
        transfer.amount = dataBinding.transferPointsVm?.amount?.toDouble()
        transfer.firstName = activity?.getPref(SharedPrefKeys.FIRST_NAME, "")
        transfer.lastName = activity?.getPref(SharedPrefKeys.LAST_NAME, "")
        transfer.mobileNumber = activity?.getPref(SharedPrefKeys.MOBILE_NUMBER, "")
        transfer.email = activity?.getPref(SharedPrefKeys.EMAIL, "")
        val displayName = activity?.getPref(SharedPrefKeys.DISPLAY_NAME, "")
        transfer.txnId = displayName + System.currentTimeMillis()
        transfer.transferToNumber = dataBinding.transferPointsVm?.transferTo
        transfer.playerId = activity?.getPref(SharedPrefKeys.PLAYER_ID, "")
        transfer.addedOn = Global.getFormatDate(Calendar.getInstance().time)
        transfer.createdOn = Global.getFormatDate(Calendar.getInstance().time)
        transfer.status = "Success"

        if (validate()) {
            showProgress()
            compositeDisposable.add(transactionService.transferPoint(transfer)
                    .processRequest(
                            { response ->
                                when {
                                    response.isSuccess -> {
                                        hideProgress()
                                        toast(response.message)
                                        val bundle = Bundle()
                                        bundle.putDouble("Amount", transfer.amount!!)
                                        bundle.putString("MobileNumber", transfer.mobileNumber)
                                        bundle.putString("Name", "${transfer.firstName} ${transfer.lastName}")
                                        firebaseAnalytics.logEvent("TransferPoints", bundle)
                                        myTrace.incrementMetric("transfer_points_success", 1)
                                        myTrace.stop()
                                        startActivity<WalletActivity>()
                                        activity?.finish()
                                    }
                                    else ->{
                                        hideProgress()
                                        myTrace.incrementMetric("transfer_points_failed", 1)
                                        myTrace.stop()
                                        showDialog(activity!!, "Error", response.message)
                                        Global.updateCrashlyticsMessage(transfer.mobileNumber!!, "Error while transfer points to other user.", "TransferPoints", Exception(response.message))
                                    }
                                }
                            },
                            { err ->
                                hideProgress()
                                myTrace.incrementMetric("transfer_points_failed", 1)
                                myTrace.stop()
                                showDialog(activity!!, "Error", err.toString())
                                Global.updateCrashlyticsMessage(transfer.mobileNumber!!, "Error while transfer points to other user.", "TransferPoints", Exception(err))
                            }
                    ))
        }

    }

    private fun validate(): Boolean {
        when {
            dataBinding.transferPointsVm?.amount.isNullOrBlank() -> {
                inputLayoutAmount.error = "Enter valid amount."
                return false
            }
            else -> inputLayoutAmount.error = null
        }
        when {
            dataBinding.transferPointsVm?.transferTo.isNullOrBlank() -> {
                inputLayoutMobileNumber.error = "Enter Mobile Number"
                return false
            }
            else -> inputLayoutMobileNumber.error = null
        }
        when {
            dataBinding.transferPointsVm?.transferTo?.length != 10 -> {
                inputLayoutMobileNumber.error = "Enter valid 10 digit mobile number."
                return false
            }
            else -> inputLayoutMobileNumber.error = null
        }

        val mobileNumber = activity?.getPref(SharedPrefKeys.MOBILE_NUMBER, "")
        when {
            mobileNumber != "" -> if (dataBinding.transferPointsVm?.transferTo == mobileNumber) {
                inputLayoutMobileNumber.error = "Mobile number must be different from your number"
                return false
            } else {
                inputLayoutMobileNumber.error = null
            }
        }

        if (!Global.checkInternet(activity!!)){
            return false
        }

        return true
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

    companion object {

    }
}
