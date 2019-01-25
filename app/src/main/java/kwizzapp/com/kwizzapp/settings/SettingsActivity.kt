package kwizzapp.com.kwizzapp.settings

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import kwizzapp.com.kwizzapp.R
import kwizzapp.com.kwizzapp.settings.bankdetails.BankDetailFragment
import kwizzapp.com.kwizzapp.settings.bankdetails.EditBankDetailsFragment
import kwizzapp.com.kwizzapp.settings.menusettings.SettingMenuFragment
import kwizzapp.com.kwizzapp.settings.policies.PoliciesFragment
import kwizzapp.com.kwizzapp.settings.policies.about.AboutAppFragment
import kwizzapp.com.kwizzapp.settings.policies.opensource.OpenSourceLicenseFragment
import kwizzapp.com.kwizzapp.settings.policies.privacypolicies.PrivacyPoliciesFragment
import kwizzapp.com.kwizzapp.settings.policies.termsnconditions.TermsNConditionFragment
import kwizzapp.com.kwizzapp.settings.profilesettings.EditProfileFragment
import kwizzapp.com.kwizzapp.settings.profilesettings.ProfileFragment
import net.rmitsolutions.mfexpert.lms.helpers.switchToFragment
import org.jetbrains.anko.find
import android.content.Intent
import kwizzapp.com.kwizzapp.Constants
import net.rmitsolutions.mfexpert.lms.helpers.logD
import org.jetbrains.anko.startActivity


class SettingsActivity : AppCompatActivity(),
        SettingMenuFragment.OnFragmentInteractionListener, ProfileFragment.OnFragmentInteractionListener,
        BankDetailFragment.OnFragmentInteractionListener, PoliciesFragment.OnFragmentInteractionListener,
        EditProfileFragment.OnFragmentInteractionListener, EditBankDetailsFragment.OnFragmentInteractionListener,
        PrivacyPoliciesFragment.OnFragmentInteractionListener, AboutAppFragment.OnFragmentInteractionListener,
        TermsNConditionFragment.OnFragmentInteractionListener, OpenSourceLicenseFragment.OnFragmentInteractionListener {

    private lateinit var toolBar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        toolBar = find(R.id.toolbar)
        setSupportActionBar(toolBar)

        val menuFragment = SettingMenuFragment()
        switchToFragment(menuFragment)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val params = Bundle()
        params.putInt("ShareApp", item?.itemId!!)
        var eventName = "EventName : Null"
        when (item.itemId) {
            R.id.mShare -> {
                eventName = "ShareApp"
                val i = Intent(android.content.Intent.ACTION_SEND)
                i.type = "text/plain"
                i.putExtra(android.content.Intent.EXTRA_TEXT, "KwizzAppp is pretty cool. Check it out on Google Play Games. See if you can beat my score!\n" +
                        "\n" +
                        "https://play.google.com/store/apps/details?id=com.kwizzapp&pcampaignid=GPG_shareGame")
                startActivity(Intent.createChooser(i, "Share Via"))
            }
        }
        Constants.firebaseAnalytics.logEvent(eventName, params)
        return super.onOptionsItemSelected(item)
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
                startActivity<SettingsActivity>()
            }
        }
    }
}
