package kwizzapp.com.kwizzapp.dashboard

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.games.Games
import kwizzapp.com.kwizzapp.Constants
import kwizzapp.com.kwizzapp.Constants.firebaseAnalytics

import kwizzapp.com.kwizzapp.R
import kwizzapp.com.kwizzapp.login.LoginFragment
import kwizzapp.com.kwizzapp.multiplayer.GameMenuFragment
import kwizzapp.com.kwizzapp.settings.SettingsActivity
import kwizzapp.com.kwizzapp.wallet.WalletActivity
import net.rmitsolutions.mfexpert.lms.helpers.*
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.startActivity


class DashboardFragment : Fragment(), View.OnClickListener {

    private var listener: OnFragmentInteractionListener? = null
    private val CLICKABLES = intArrayOf(R.id.playLayout, R.id.achievemnetLayout, R.id.leaderboardLayout, R.id.walletLayout, R.id.logoutLayout, R.id.settingLayout)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        for (id in CLICKABLES) {
            view.find<CardView>(id).setOnClickListener(this)
        }
        return view
    }

    override fun onClick(v: View?) {
        val params = Bundle()
        params.putInt("DashboardEvents", v?.id!!)
        var eventName: String ? = null
        when (v.id) {
            R.id.logoutLayout -> {
                eventName = "SignOut"
                signOut()
            }
            R.id.achievemnetLayout -> {
                eventName = "ShowAchievements"
                showAchievements()
            }
            R.id.leaderboardLayout -> {
                eventName = "ShowLeaderboards"
                showLeaderBoards()
            }
            R.id.walletLayout -> {
                eventName = "OpenWallet"
                startActivity<WalletActivity>()
            }
            R.id.playLayout -> {
                eventName = "OpenPlay"
                openGameMenuFragment()
            }
            R.id.settingLayout -> {
                eventName = "OpenSettings"
                startActivity<SettingsActivity>()
            }
        }
        firebaseAnalytics.logEvent(eventName!!, params)
    }

    private fun openGameMenuFragment() {
        val gameMenu = GameMenuFragment()
        switchToFragmentBackStack(gameMenu)
    }


    private fun showAchievements() {
        Games.getAchievementsClient(activity!!, GoogleSignIn.getLastSignedInAccount(activity)!!)
                .achievementsIntent
                .addOnSuccessListener { intent -> startActivityForResult(intent, Constants.RC_ACHIEVEMENT_UI) }
    }

    private fun showLeaderBoards() {
        Games.getLeaderboardsClient(activity!!, GoogleSignIn.getLastSignedInAccount(activity)!!)
                .getLeaderboardIntent(getString(R.string.leaderboard_toppers))
                .addOnSuccessListener { intent -> startActivityForResult(intent, Constants.RC_LEADERBOARD_UI) }
    }

    private fun signOut() {
        when {
            isSignedIn() -> {
                val signInClient = GoogleSignIn.getClient(activity!!, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                signInClient.signOut().addOnCompleteListener(activity!!) {
                    // at this point, the user is signed out.
                    toast("Sign out successfully!")
                    activity?.clearPrefs()
                    val loginFragment = LoginFragment()
                    switchToFragment(loginFragment)
                }
            }
            else -> toast("Already signed out!")
        }
    }

    private fun isSignedIn(): Boolean {
        return GoogleSignIn.getLastSignedInAccount(activity) != null
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
