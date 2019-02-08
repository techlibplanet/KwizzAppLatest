package kwizzapp.com.kwizzapp.login

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mayank.kwizzapp.libgame.LibGameConstants
import com.example.mayank.kwizzapp.libgame.LibPlayGame
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.games.Games
import com.google.android.gms.games.InvitationsClient
import com.google.android.gms.games.RealTimeMultiplayerClient
import com.shobhitpuri.custombuttons.GoogleSignInButton
import kwizzapp.com.kwizzapp.R
import kwizzapp.com.kwizzapp.dashboard.DashboardFragment
import kwizzapp.com.kwizzapp.helper.Global
import kwizzapp.com.kwizzapp.userInfo.UserInfoFragment
import net.rmitsolutions.mfexpert.lms.helpers.*
import org.jetbrains.anko.find
import java.lang.Exception


class LoginFragment : Fragment(), View.OnClickListener {

    private var listener: OnFragmentInteractionListener? = null
    private lateinit var loginButton: GoogleSignInButton
    private val RC_SIGN_IN = 101
    private var mRealTimeMultiplayerClient: RealTimeMultiplayerClient? = null
    private var mInvitationsClient: InvitationsClient? = null
    private var mPlayerId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        loginButton = view.find(R.id.buttonSignIn)
        loginButton.setOnClickListener(this)
        return view
    }


    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
        showProgress()
        signInSilently()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar?.show()
    }

    private fun signInSilently() {
        val signInClient = GoogleSignIn.getClient(activity!!,
                GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
        signInClient.silentSignIn().addOnCompleteListener(activity!!
        ) { task ->
            if (task.isSuccessful) {
                // The signed in account is stored in the task's result.
                val signedInAccount = task.result
                onConnected(signedInAccount!!)
            } else {
                hideProgress()
                //val mobileNumber = activity?.getPref(SharedPrefKeys.MOBILE_NUMBER, "")
                //Global.updateCrashlyticsMessage(mobileNumber!!, "Sign In Silently Error", "Silent Sign In", task.exception!!)
                logD("SignInSilently Incomplete")
                // Player will need to sign-in explicitly using via UI
            }
        }
    }

    private fun onConnected(signInAccount: GoogleSignInAccount) {
        val gameClient = Games.getGamesClient(activity!!, GoogleSignIn.getLastSignedInAccount(activity)!!)
        gameClient.setViewForPopups(activity!!.find(android.R.id.content))
        // update the clients
        mRealTimeMultiplayerClient = Games.getRealTimeMultiplayerClient(activity!!, signInAccount)
        mInvitationsClient = Games.getInvitationsClient(activity!!, signInAccount)

        // get the playerId from the PlayersClient
        val playersClient = Games.getPlayersClient(activity!!, signInAccount)
        playersClient.currentPlayer.addOnSuccessListener { player ->
            mPlayerId = player.playerId

            activity?.putPref(SharedPrefKeys.PLAYER_ID, mPlayerId)
            activity?.putPref(SharedPrefKeys.DISPLAY_NAME, player.displayName)
            activity?.putPref(SharedPrefKeys.HIGH_RES_URI, player.hiResImageUri.toString())
            activity?.putPref(SharedPrefKeys.ICON_IMAGE_URI, player.iconImageUri.toString())
            var nameArray: List<String>? = null
            if (player.name != null) {
                nameArray = player.name.split(" ")
            }
            val firstName = nameArray?.get(0)
            val lastName = nameArray?.get(1)
            if (firstName != null && lastName != null) {
                activity?.putPref(SharedPrefKeys.FIRST_NAME, firstName)
                activity?.putPref(SharedPrefKeys.LAST_NAME, lastName)
            }
            val email = activity?.getPref(SharedPrefKeys.EMAIL, "")
            val mobileNumber = activity?.getPref(SharedPrefKeys.MOBILE_NUMBER, "")
            val libPlayGame = LibPlayGame(activity!!)
            LibGameConstants.GameConstants.mInvitationClient = Games.getInvitationsClient(activity!!, libPlayGame.getSignInAccount()!!)
            LibGameConstants.GameConstants.mInvitationClient?.registerInvitationCallback(libPlayGame.mInvitationCallbackHandler)
            hideProgress()
            if (email == "" && mobileNumber == "") {
                val userInfoFragment = UserInfoFragment()
                switchToFragment(userInfoFragment)
            } else {
                val dashboardFrag = DashboardFragment()
                switchToFragment(dashboardFrag)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.buttonSignIn -> {
                if (activity?.isNetConnected()!!) {
                    startSignInIntent()
                } else {
                    toast("No Internet !")
                }
            }
        }
    }

    private fun startSignInIntent() {
        val signInClient = GoogleSignIn.getClient(activity!!, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
        val intent = signInClient.signInIntent
        startActivityForResult(intent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        logD("Inside fragment activity result")
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                // The signed in account is stored in the result.
                val signedInAccount = result.signInAccount
                onConnected(signedInAccount!!)
            } else {
                var message = result.status.statusMessage
                if (message == null || message.isEmpty()) {
                    message = getString(R.string.signin_other_error)
                }
                showDialog(activity!!, " Error", message)
                val mobileNumber = activity?.getPref(SharedPrefKeys.MOBILE_NUMBER, "")
                Global.updateCrashlyticsMessage(mobileNumber!!,"OnActivityResult Sign In Error", "Login button click", Exception(message))

            }
        }
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
