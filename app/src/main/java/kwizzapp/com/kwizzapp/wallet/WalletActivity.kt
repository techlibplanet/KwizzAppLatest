package kwizzapp.com.kwizzapp.wallet

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.mayank.kwizzapp.dependency.components.DaggerInjectActivityComponent
import com.example.mayank.kwizzapp.wallet.AddPointsFragment
import com.example.mayank.kwizzapp.wallet.TransferPointsFragment
import com.example.mayank.kwizzapp.wallet.WalletMenuFragment
import com.example.mayank.kwizzapp.wallet.WithdrawalPointsFragment
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import io.reactivex.disposables.CompositeDisposable
import kwizzapp.com.kwizzapp.Constants.firebaseAnalytics
import kwizzapp.com.kwizzapp.KwizzApp
import kwizzapp.com.kwizzapp.R
import kwizzapp.com.kwizzapp.helper.Global
import kwizzapp.com.kwizzapp.helper.processRequest
import kwizzapp.com.kwizzapp.models.Transactions
import kwizzapp.com.kwizzapp.services.IRazorpay
import kwizzapp.com.kwizzapp.services.ITransaction
import net.rmitsolutions.mfexpert.lms.helpers.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import java.lang.Exception
import javax.inject.Inject

class WalletActivity : AppCompatActivity(), WalletMenuFragment.OnFragmentInteractionListener,
        AddPointsFragment.OnFragmentInteractionListener, WithdrawalPointsFragment.OnFragmentInteractionListener,
        TransferPointsFragment.OnFragmentInteractionListener, TransactionFragment.OnFragmentInteractionListener,
        PaymentResultListener {

    private lateinit var compositeDisposable: CompositeDisposable
    @Inject
    lateinit var transactionService: ITransaction
    private lateinit var myTrace: Trace

    @Inject
    lateinit var razorpayService: IRazorpay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet)

        val depComponent = DaggerInjectActivityComponent.builder()
                .applicationComponent(KwizzApp.applicationComponent)
                .build()
        depComponent.injectWalletActivity(this)

        compositeDisposable = CompositeDisposable()

        myTrace = FirebasePerformance.getInstance().newTrace("add_points_trace")

        val walletMenuFragment = WalletMenuFragment()
        switchToFragment(walletMenuFragment)
    }

    override fun onFragmentInteraction(uri: Uri) {

    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        when (count) {
            0 -> super.onBackPressed()
            1 -> supportFragmentManager.popBackStack()
            else -> {
                finish()
                startActivity<WalletActivity>()
            }
        }
    }

    override fun onPaymentError(errorCode: Int, response: String?) {
        hideProgress()
        when (errorCode) {
            Checkout.NETWORK_ERROR -> toast("Network error occourred")
            Checkout.PAYMENT_CANCELED -> toast("Payment Cancelled")
            Checkout.INVALID_OPTIONS -> toast("Invalid Options")
            Checkout.TLS_ERROR -> toast("Device doesn't have TLS 1.1 or TLS 1.2")
        }
    }


    override fun onPaymentSuccess(razorpayPaymentID: String?) {
        getTransactionDetails(razorpayPaymentID!!)
    }

    // Get the transaction details using Razorpay
    private fun getTransactionDetails(paymentId: String) {
        myTrace.start()
        val payment = Transactions.AddPointToServer()
        payment.paymentId = paymentId
        payment.firstName = getPref(SharedPrefKeys.FIRST_NAME, "")
        payment.lastName = getPref(SharedPrefKeys.LAST_NAME, "")
        payment.mobileNumber = getPref(SharedPrefKeys.MOBILE_NUMBER, "")
        payment.playerId = getPref(SharedPrefKeys.PLAYER_ID, "")
        payment.transactionType = "Credited"
        payment.addedOn = System.currentTimeMillis().toString()
        payment.createdOn = System.currentTimeMillis().toString()
        payment.status = "success"
        compositeDisposable.add(razorpayService.getTransactionByPaymentId(payment)
                .processRequest(
                        { transactions ->
                            hideProgress()
                            if (transactions.isSuccess) {
                                val bundle = Bundle()
                                bundle.putDouble("Amount", transactions.balance)
                                bundle.putString("MobileNumber", transactions.mobileNumber)
                                bundle.putString("Name", "${getPref(SharedPrefKeys.FIRST_NAME, "")} ${getPref(SharedPrefKeys.LAST_NAME, "")}")
                                firebaseAnalytics.logEvent("AddPoints", bundle)
                                toast(transactions.message)
                                myTrace.incrementMetric("add_points_success", 1)
                                myTrace.stop()
                                finish()
                                startActivity<WalletActivity>()
                            } else {
                                myTrace.incrementMetric("add_points_failed", 1)
                                myTrace.stop()
                                showDialog(this, "Error", transactions.message)
                                Global.updateCrashlyticsMessage(transactions.mobileNumber!!, "Error while adding points to server", "AddPoints", Exception(transactions.message))
                            }

                        },
                        { err ->
                            hideProgress()
                            toast("Error - $err")
                            myTrace.incrementMetric("add_points_failed", 1)
                            myTrace.stop()
                            showDialog(this, "Error", err.toString())
                            Global.updateCrashlyticsMessage(getPref(SharedPrefKeys.MOBILE_NUMBER, ""), "Error while adding points to server", "AddPoints", Exception(err))
                        }
                )
        )
    }
}
