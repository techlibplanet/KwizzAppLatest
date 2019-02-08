package kwizzapp.com.kwizzapp.wallet

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import org.jetbrains.anko.find
import android.support.v7.app.AppCompatActivity
import com.example.mayank.kwizzapp.dependency.components.DaggerInjectFragmentComponent
import com.example.mayank.kwizzapp.transactions.TransactionAdapter
import com.example.mayank.kwizzapp.transactions.TransactionDetailsVm
import io.reactivex.disposables.CompositeDisposable
import kwizzapp.com.kwizzapp.Constants
import kwizzapp.com.kwizzapp.KwizzApp
import kwizzapp.com.kwizzapp.R
import kwizzapp.com.kwizzapp.helper.Global
import kwizzapp.com.kwizzapp.helper.processRequest
import kwizzapp.com.kwizzapp.services.ITransaction
import net.rmitsolutions.mfexpert.lms.helpers.*
import java.lang.Exception
import javax.inject.Inject


class TransactionFragment : Fragment() {
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var recyclerView: RecyclerView
    val adapter: TransactionAdapter by lazy { TransactionAdapter() }
    lateinit var modelList: MutableList<TransactionDetailsVm>
    private lateinit var toolBar: Toolbar

    @Inject
    lateinit var transactionService: ITransaction
    private lateinit var compositeDisposable: CompositeDisposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val depComponent = DaggerInjectFragmentComponent.builder()
                .applicationComponent(KwizzApp.applicationComponent)
                .build()
        depComponent.injectTransactionFragment(this)
        compositeDisposable = CompositeDisposable()
        showProgress()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_transaction, container, false)

        toolBar = view.find(R.id.toolbar)
        toolBar.title = "Transactions"
        (activity as AppCompatActivity).setSupportActionBar(toolBar)
        recyclerView = view.findViewById(R.id.transactions_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)
        recyclerView.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        recyclerView.adapter = adapter
        modelList = mutableListOf<TransactionDetailsVm>()
        setTransactionsItems()
        return view
    }

    private fun setTransactionsItems() {
        modelList.clear()
        val mobileNumber = activity?.getPref(SharedPrefKeys.MOBILE_NUMBER, "")
        if (validate()) {
            if (mobileNumber != "") {
                compositeDisposable.add(transactionService.fetchTransactions(mobileNumber!!)
                        .processRequest(
                                { response ->
                                    if (response.isSuccess) {
                                        val transactions = response.transactions
                                        for (data in transactions) {
                                            val transactions = TransactionDetailsVm()
                                            when {
                                                data.transferTo == "" && data.receivedFrom == "" -> transactions.textUserName = "${data.firstName} ${data.lastName}"
                                                data.transactionType.toString() == Constants.TRANSACTION_TYPE_DEBITED -> transactions.textUserName = data.transferTo
                                                else -> transactions.textUserName = data.receivedFrom
                                            }
                                            transactions.transactionType = data.transactionType
                                            transactions.textAmount = "${data.amount}"
                                            transactions.textTimeStamp = data.createdOn
                                            transactions.textDescription = data.productInfo
                                            modelList.add(transactions)
                                        }
                                    } else {
                                        hideProgress()
                                        showDialog(activity!!, "Response", response.message)
                                        Global.updateCrashlyticsMessage(mobileNumber, "Error while fetching transactions from server.", "FetchTransactions", Exception(response.message))
                                    }
                                    setRecyclerViewAdapter(modelList)
                                },
                                { err ->
                                    hideProgress()
                                    showDialog(activity!!, "Error", err.toString())
                                    Global.updateCrashlyticsMessage(mobileNumber, "Error while fetching transactions from server.", "FetchTransactions", Exception(err))
                                }
                        ))
            } else {
                hideProgress()
                toast("Enter mobile number in settings")
            }
        } else {
            hideProgress()
        }
    }

    private fun validate(): Boolean {
        if (!Global.checkInternet(activity!!)) {
            return false
        }
        return true
    }

    private fun setRecyclerViewAdapter(list: List<TransactionDetailsVm>) {
        hideProgress()
        adapter.items = list
        adapter.notifyDataSetChanged()
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
        compositeDisposable.dispose()
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
    }
}
