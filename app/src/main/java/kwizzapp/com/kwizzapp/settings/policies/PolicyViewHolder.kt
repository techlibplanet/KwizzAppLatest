package kwizzapp.com.kwizzapp.settings.policies

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import kotlinx.android.synthetic.main.policies_row.view.*
import kwizzapp.com.kwizzapp.databinding.PolicyItemBinding
import kwizzapp.com.kwizzapp.settings.policies.about.AboutAppFragment
import kwizzapp.com.kwizzapp.settings.policies.opensource.OpenSourceLicenseFragment
import kwizzapp.com.kwizzapp.settings.policies.privacypolicies.PrivacyPoliciesFragment
import kwizzapp.com.kwizzapp.settings.policies.termsnconditions.TermsNConditionFragment
import kwizzapp.com.kwizzapp.viewmodels.SettingVm
import net.rmitsolutions.mfexpert.lms.helpers.switchToFragmentBackStack

class PolicyViewHolder(val dataBinding: PolicyItemBinding) : RecyclerView.ViewHolder(dataBinding.root) {

    fun bindView(context : Context, policyVm: SettingVm.SettingMenuVm, position : Int){
        dataBinding.policyItemVm = policyVm

        dataBinding.root.policy_layout.setOnClickListener {
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