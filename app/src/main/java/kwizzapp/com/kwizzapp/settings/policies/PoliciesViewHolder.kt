package com.example.mayank.kwizzapp.policies

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import kwizzapp.com.kwizzapp.databinding.PolicyItemBinding
import kwizzapp.com.kwizzapp.settings.policies.about.AboutAppFragment
import kwizzapp.com.kwizzapp.settings.policies.opensource.OpenSourceLicenseFragment
import kwizzapp.com.kwizzapp.settings.policies.privacypolicies.PrivacyPoliciesFragment
import kwizzapp.com.kwizzapp.settings.policies.termsnconditions.TermsNConditionFragment
import kwizzapp.com.kwizzapp.viewmodels.SettingVm
import net.rmitsolutions.mfexpert.lms.helpers.switchToFragmentBackStack

class PoliciesViewHolder(val dataBinding: PolicyItemBinding) : RecyclerView.ViewHolder(dataBinding.root) {

    fun bindView(context: Context, policyVm: SettingVm.PoliciesVm, position : Int) {
        dataBinding.policyItemVm= policyVm

        dataBinding.root.setOnClickListener{
            Log.d("TAG", "$position")
            when(position){
                0 ->{
                    val fragment = AboutAppFragment()
                    context.switchToFragmentBackStack(fragment)
                }
                1 ->{
                    val fragment = PrivacyPoliciesFragment()
                    context.switchToFragmentBackStack(fragment)
                }
                2 ->{
                    val fragment = TermsNConditionFragment()
                    context.switchToFragmentBackStack(fragment)
                }
                3 ->{
                    val fragment = OpenSourceLicenseFragment()
                    context.switchToFragmentBackStack(fragment)
                }
                else ->{
                    Log.d("TAG", "Position - $position")
                }
            }
        }
    }
}

//class PoliciesViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
//    private lateinit var context : Context
//
//    fun bindView(context: Context, policiesVm: SettingVm.PoliciesVm, position: Int){
//        this.context = context
//        val textLabel = itemView.find<TextView>(R.id.policies_label)
//        val imageIcon = itemView.find<ImageView>(R.id.policies_icon)
////        val textLabel = itemView.find<TextView>(R.id.policies_label)
////        val imageIcon = itemView.find<ImageView>(R.id.policies_icon)
//
//        textLabel.text = policiesVm.textLabel
//        imageIcon.setImageResource(policiesVm.policiesIcon)
//
//
//        itemView.setOnClickListener{
//            context.toast("$position Clicked")
//        }
//
////        textLabel.text = policiesVm.textLabel
////        imageIcon.setImageResource(policiesVm.policiesIcon)
//
////        itemView.setOnClickListener{
//            when(position){
//                0 ->{
//                    val fragment = AboutAppFragment()
//                    context.switchToFragmentBackStack(fragment)
//                }
//                1 ->{
//                    val fragment = PrivacyPoliciesFragment()
//                    context.switchToFragmentBackStack(fragment)
//                }
//                2 ->{
//                    val fragment = TermsNConditionFragment()
//                    context.switchToFragmentBackStack(fragment)
//                }
//                3 ->{
//                    val fragment = OpenSourceLicenseFragment()
//                    context.switchToFragmentBackStack(fragment)
//                }
//                else ->{
//                    Log.d("TAG", "Position - $position")
//                }
//            }
////        }
//    }
//}
