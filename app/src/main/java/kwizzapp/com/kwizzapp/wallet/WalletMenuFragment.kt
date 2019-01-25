package com.example.mayank.kwizzapp.wallet

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mayank.kwizzapp.dependency.components.DaggerInjectFragmentComponent
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.wallet_menu_layout.*
import kwizzapp.com.kwizzapp.Constants
import kwizzapp.com.kwizzapp.KwizzApp
import kwizzapp.com.kwizzapp.R
import kwizzapp.com.kwizzapp.helper.Global
import kwizzapp.com.kwizzapp.helper.processRequest
import kwizzapp.com.kwizzapp.models.Transactions
import kwizzapp.com.kwizzapp.services.ITransaction
import kwizzapp.com.kwizzapp.wallet.TransactionFragment
import net.rmitsolutions.mfexpert.lms.helpers.*
import org.jetbrains.anko.find
import java.lang.Exception
import javax.inject.Inject

class WalletMenuFragment : Fragment(), View.OnClickListener {
    @Inject
    lateinit var transactionService: ITransaction
    private lateinit var compositeDisposable: CompositeDisposable
    private var listener: OnFragmentInteractionListener? = null
    private val CLICKABLES = intArrayOf(R.id.addPointsLayout, R.id.withdrawalLayout, R.id.transferLayout, R.id.transactionLayout)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        val depComponent = DaggerInjectFragmentComponent.builder()
                .applicationComponent(KwizzApp.applicationComponent)
                .build()
        depComponent.injectWalletMenuFragment(this)
        compositeDisposable = CompositeDisposable()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_wallet_menu, container, false)
        // Getting balance from server
        when {
            activity?.isNetConnected()!! -> checkBalance()
            else -> {
                toast("No Internet")
                walletPoints.text = "Error - No Internet !"
            }
        }
        for (id in CLICKABLES) view.find<CardView>(id).setOnClickListener(this)
        return view
    }

    override fun onClick(v: View?) {
        val params = Bundle()
        params.putInt("WalletButton", v?.id!!)
        var eventName : String? = null
        when (v.id) {
            R.id.addPointsLayout -> {
                eventName = "AddPoints"
                val addPointsFragment = AddPointsFragment()
                switchToFragmentBackStack(addPointsFragment)
            }

            R.id.withdrawalLayout -> {
                eventName = "WithdrawalPoints"
                val withdrawalPointsFragment = WithdrawalPointsFragment()
                switchToFragmentBackStack(withdrawalPointsFragment)
            }

            R.id.transferLayout -> {
                eventName = "TransferPoints"
                val transferPointsFragment = TransferPointsFragment()
                switchToFragmentBackStack(transferPointsFragment)
            }

            R.id.transactionLayout -> {
                eventName = "ShowTransactions"
                val transactionFragment = TransactionFragment()
                switchToFragmentBackStack(transactionFragment)
            }
        }
        Constants.firebaseAnalytics.logEvent(eventName!!, params)
    }

    private fun checkBalance() {
        showProgress()
        val checkBalance = Transactions.CheckBalance()
        checkBalance.mobileNumber = activity?.getPref(SharedPrefKeys.MOBILE_NUMBER, "")
        when {
            checkBalance.mobileNumber != "" -> compositeDisposable.add(transactionService.checkBalance(checkBalance)
                    .processRequest(
                            { response ->
                                when {
                                    response.isSuccess -> {
                                        hideProgress()
                                        val amount = Global.doubleValueFormat(response.balance)
                                        walletPoints.text = "Points - ${amount} ${activity?.getString(R.string.rupeeSymbol)}"
                                    }
                                    else -> {
                                        hideProgress()
                                        walletPoints.text = "Failed !"
                                        Global.updateCrashlyticsMessage(checkBalance.mobileNumber!!,"Error while checking balance on wallet menu fragment.", "CheckBalance", Exception(response.message))
                                    }
                                }
                            },
                            { err ->
                                hideProgress()
                                walletPoints.text = "Failed !"
                                showDialog(activity!!, "Error", err.toString())
                                Global.updateCrashlyticsMessage(checkBalance.mobileNumber!!,"Error while checking balance on wallet menu fragment.", "CheckBalance", Exception(err))
                            }
                    ))
            else -> {
            }
        }

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
