package kwizzapp.com.kwizzapp.multiplayer

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mayank.kwizzapp.dependency.components.DaggerInjectFragmentComponent
import com.example.mayank.kwizzapp.libgame.LibPlayGame
import io.reactivex.disposables.CompositeDisposable
import kwizzapp.com.kwizzapp.Constants
import kwizzapp.com.kwizzapp.KwizzApp

import kwizzapp.com.kwizzapp.R
import kwizzapp.com.kwizzapp.helper.Global
import kwizzapp.com.kwizzapp.helper.Global.Companion.checkInternet
import kwizzapp.com.kwizzapp.helper.processRequest
import kwizzapp.com.kwizzapp.models.Transactions
import kwizzapp.com.kwizzapp.services.ITransaction
import kwizzapp.com.kwizzapp.singleplay.SinglePlayMenuFragment
import kwizzapp.com.kwizzapp.wallet.WalletActivity
import net.rmitsolutions.mfexpert.lms.helpers.*
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.startActivity
import java.lang.Exception
import javax.inject.Inject

class GameMenuFragment : Fragment(), View.OnClickListener {

    @Inject
    lateinit var transactionService: ITransaction
    private var listener: OnFragmentInteractionListener? = null
    private val CLICKABLES = intArrayOf(R.id.singlePlayerLayout, R.id.quickGameLayout, R.id.multiplayerLayout, R.id.invitationLayout)
    private var check: Int = -1
    private lateinit var libPlayGame: LibPlayGame
    private lateinit var compositeDisposable: CompositeDisposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        libPlayGame = LibPlayGame(activity!!)
        compositeDisposable = CompositeDisposable()
        val depComponent = DaggerInjectFragmentComponent.builder()
                .applicationComponent(KwizzApp.applicationComponent)
                .build()
        depComponent.injectGameMenuFragment(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_game_menu, container, false)

        for (id in CLICKABLES) {
            view.find<CardView>(id).setOnClickListener(this)
        }
        return view
    }

    override fun onClick(v: View?) {
        val params = Bundle()
        params.putInt("GameMode", v?.id!!)
        var gameModeName: String? = null

        when (v.id) {
            R.id.singlePlayerLayout -> {
                if (checkInternet(activity!!)) {
                    gameModeName = "SinglePlayer"
                    val singlePlayDetails = SinglePlayMenuFragment()
                    switchToFragmentBackStack(singlePlayDetails)
//                    toast("Coming soon...")
                }
            }
            R.id.quickGameLayout -> {
                if (checkInternet(activity!!)) {
                    gameModeName = "QuickGame"
                    showProgress()
                    check = 0
                    checkBalance()
                }
            }
            R.id.multiplayerLayout -> {
                if (checkInternet(activity!!)) {
                    gameModeName = "MultiPlayer"
                    showProgress()
                    check = 1
                    checkBalance()
                }
            }
            R.id.invitationLayout -> {
                if (checkInternet(activity!!)) {
                    gameModeName = "Invitations"
                    showProgress()
                    libPlayGame.showInvitationInbox()
                }
            }
        }
        Constants.firebaseAnalytics.logEvent(gameModeName!!, params)
    }


    private fun checkBalance() {
        val checkBalance = Transactions.CheckBalance()
        checkBalance.mobileNumber = activity?.getPref(SharedPrefKeys.MOBILE_NUMBER, "")
        if (checkBalance.mobileNumber != "") {
            compositeDisposable.add(transactionService.checkBalance(checkBalance)
                    .processRequest({ response ->
                        if (response.isSuccess) {
                            if (response.balance >= 10.00) {
                                if (check == 0) {
                                    libPlayGame.startQuickGame()
                                } else {
                                    libPlayGame.invitePlayers()
                                }
                                check = -1
                            } else {
                                AlertDialog.Builder(activity!!).setCancelable(false).setTitle("Message")
                                        .setMessage(activity?.getString(R.string.insufficient_balance))
                                        .setPositiveButton("Add Points") { dialog, which ->
                                            hideProgress()
                                            startActivity<WalletActivity>()
                                            activity?.finish()
                                            dialog.dismiss()
                                        }.setNegativeButton("Cancel") { dialog, which ->
                                            hideProgress()
                                            dialog.dismiss()
                                        }.show()
                            }
                        } else {
                            showDialog(activity!!, "Error", response.message)
                            Global.updateCrashlyticsMessage(checkBalance.mobileNumber!!, "Error while checking points on Game Menu Fragment", "CheckBalance", Exception(response.message))
                        }
                    }, { err ->
                        hideProgress()
                        showDialog(activity!!, "Error", err.toString())
                        Global.updateCrashlyticsMessage(checkBalance.mobileNumber!!, "Error while checking points on Game Menu Fragment", "CheckBalance", Exception(err))
                    }))
        } else {
            hideProgress()
            showDialog(activity!!, "Error", "Update mobile number to continue!")
        }
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

}

