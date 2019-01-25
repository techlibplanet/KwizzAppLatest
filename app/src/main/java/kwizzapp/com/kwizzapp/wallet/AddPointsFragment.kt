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
import com.technoholicdeveloper.kwizzapp.gateway.PayUMoney
import kotlinx.android.synthetic.main.add_points_layout.*
import kwizzapp.com.kwizzapp.R
import kwizzapp.com.kwizzapp.databinding.AddPointsFragmentBinding
import kwizzapp.com.kwizzapp.viewmodels.AddPoints
import net.rmitsolutions.mfexpert.lms.helpers.*
import org.jetbrains.anko.find

class AddPointsFragment : Fragment(), View.OnClickListener {

    private var listener: OnFragmentInteractionListener? = null
    private lateinit var dataBinding: AddPointsFragmentBinding
    private lateinit var addPointsVm: AddPoints
    private lateinit var payUMoney: PayUMoney

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        payUMoney = PayUMoney(activity!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_points, container, false)
        val view = dataBinding.root
        addPointsVm = AddPoints()
        dataBinding.addPointsVm = addPointsVm
        view.find<Button>(R.id.buttonPay).setOnClickListener(this)
        return view
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.buttonPay -> {
                if (validate()) addPoints()
            }
        }
    }

    private fun validate(): Boolean {
        if (dataBinding.addPointsVm?.amount.isNullOrBlank()){
            inputLayoutAmount.error = " Enter valid Amount"
            return false
        }else {
            inputLayoutAmount.error = null
        }
        return true
    }

    private fun addPoints() {
        dataBinding.addPointsVm?.mobileNumber = activity?.getPref(SharedPrefKeys.MOBILE_NUMBER, "")
        dataBinding.addPointsVm?.email = activity?.getPref(SharedPrefKeys.EMAIL, "")
        dataBinding.addPointsVm?.firstName = activity?.getPref(SharedPrefKeys.FIRST_NAME, "")
        dataBinding.addPointsVm?.product = SharedPrefKeys.PRODUCT_RECHARGE_POINTS

        payUMoney.launchPayUMoney(dataBinding.addPointsVm?.amount?.toDouble()!!, dataBinding.addPointsVm?.firstName!!,
                dataBinding.addPointsVm?.mobileNumber!!, dataBinding.addPointsVm?.email!!,dataBinding.addPointsVm?.product!!)

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
