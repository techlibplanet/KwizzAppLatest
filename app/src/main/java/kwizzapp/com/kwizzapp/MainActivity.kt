package kwizzapp.com.kwizzapp


import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import com.crashlytics.android.Crashlytics
import com.example.mayank.kwizzapp.dependency.components.DaggerInjectActivityComponent
import com.example.mayank.kwizzapp.libgame.LibGameConstants
import com.example.mayank.kwizzapp.libgame.LibPlayGame
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.games.Games
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.iid.FirebaseInstanceId
import io.fabric.sdk.android.Fabric
import io.reactivex.disposables.CompositeDisposable
import kwizzapp.com.kwizzapp.dashboard.DashboardFragment
import kwizzapp.com.kwizzapp.helper.Global
import kwizzapp.com.kwizzapp.helper.processRequest
import kwizzapp.com.kwizzapp.login.LoginFragment
import kwizzapp.com.kwizzapp.models.Users
import kwizzapp.com.kwizzapp.multiplayer.GameMenuFragment
import kwizzapp.com.kwizzapp.multiplayer.MultiplayerMenuFragment
import kwizzapp.com.kwizzapp.multiplayer.MultiplayerResultFragment
import kwizzapp.com.kwizzapp.quiz.QuizFragment
import kwizzapp.com.kwizzapp.services.IUser
import kwizzapp.com.kwizzapp.singleplay.SinglePlayMenuFragment
import kwizzapp.com.kwizzapp.singleplay.SinglePlayQuizFragment
import kwizzapp.com.kwizzapp.singleplay.SinglePlayResultFragment
import kwizzapp.com.kwizzapp.userInfo.UserInfoFragment
import net.rmitsolutions.mfexpert.lms.helpers.*
import org.jetbrains.anko.find
import org.jetbrains.anko.toast
import java.lang.Exception
import javax.inject.Inject
import android.widget.Toast
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import org.jetbrains.anko.startActivity
import android.content.pm.PackageManager
import android.os.Build
import kwizzapp.com.kwizzapp.Constants.firebaseAnalytics


class MainActivity : AppCompatActivity(),
        DashboardFragment.OnFragmentInteractionListener, LoginFragment.OnFragmentInteractionListener,
        UserInfoFragment.OnFragmentInteractionListener, SinglePlayMenuFragment.OnFragmentInteractionListener,
        GameMenuFragment.OnFragmentInteractionListener, MultiplayerMenuFragment.OnFragmentInteractionListener,
        QuizFragment.OnFragmentInteractionListener, MultiplayerResultFragment.OnFragmentInteractionListener,
        SinglePlayQuizFragment.OnFragmentInteractionListener, SinglePlayResultFragment.OnFragmentInteractionListener {

//    companion object {
//        lateinit var firebaseAnalytics : FirebaseAnalytics
//    }

    @Inject
    lateinit var userService: IUser
    private lateinit var toolBar: Toolbar
    private lateinit var compositeDisposable: CompositeDisposable
    private var back_pressed = 0L
    private lateinit var libPlayGame: LibPlayGame
    private lateinit var firebaseRemoteConfig: FirebaseRemoteConfig


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

        firebaseRemoteConfig.fetch(0).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                logD("Remote message fetched successfully.")
                firebaseRemoteConfig.activateFetched()
            } else {
                logD("Remote message fetched failed")
            }
            getAppVersionCode()
        }

        // Update Fcm token if change
        Global.retrieveCurrentFcmToken().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result?.token
                val fcmToken = getPref(SharedPrefKeys.FCM_TOKEN_ID, "")
                logD("Token - $token")
                logD("Saved Token - $token")
                if (fcmToken != token) {
                    val mobileNumber = getPref(SharedPrefKeys.MOBILE_NUMBER, "")
                    if (mobileNumber != "") {
                        val updateFcmToken = Users.UpdateFcmToken()
                        updateFcmToken.mobileNumber = mobileNumber
                        updateFcmToken.fcmTokenId = token
                        updateFcmToken.firebaseInstanceId = FirebaseInstanceId.getInstance().id
                        updateFcmToken(updateFcmToken)
                    } else {
                        putPref(SharedPrefKeys.FCM_TOKEN_ID, token)
                    }
                }
            } else {
                val mobileNumber = getPref(SharedPrefKeys.MOBILE_NUMBER, "")
                logD("Task Exception - ${task.exception}")
                if (mobileNumber != "") {
                    Global.updateCrashlyticsMessage(mobileNumber, "Error in getting FCM token", "FCM Token Failed", task.exception!!)
                }
            }
        }

        toolBar = find(R.id.toolbar)
        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        Fabric.with(this, Crashlytics())

        val depComponent = DaggerInjectActivityComponent.builder()
                .applicationComponent(KwizzApp.applicationComponent)
                .build()
        depComponent.injectMainActivity(this)

        compositeDisposable = CompositeDisposable()

//        val userInfoFragment = GameMenuFragment()
//        switchToFragment(userInfoFragment)

        if (isSignedIn()) {
            val mobileNumber = getPref(SharedPrefKeys.MOBILE_NUMBER, "")
            if (mobileNumber != "") {
                val playersClient = Games.getPlayersClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                playersClient.currentPlayer.addOnSuccessListener { player ->
                    if (player.displayName == getPref(SharedPrefKeys.DISPLAY_NAME, "")) {
                        val libPlayGame = LibPlayGame(this)
                        addFireBase()
                        LibGameConstants.GameConstants.mInvitationClient = Games.getInvitationsClient(this, libPlayGame.getSignInAccount()!!)
                        LibGameConstants.GameConstants.mInvitationClient?.registerInvitationCallback(libPlayGame.mInvitationCallbackHandler)
                        val dashboardFragment = DashboardFragment()
                        switchToFragment(dashboardFragment)
                    } else {
                        val updateDisplayName = Users.UpdateDisplayName()
                        updateDisplayName.displayName = player.displayName
                        updateDisplayName.mobileNumber = getPref(SharedPrefKeys.MOBILE_NUMBER, "")
                        updateDisplayName(updateDisplayName)
                    }
                }
            } else {
                val userInfoFragment = UserInfoFragment()
                switchToFragment(userInfoFragment)
            }
        } else {
            val loginFragment = LoginFragment()
            switchToFragment(loginFragment)
        }
    }

    // Get App Version and show popup for update app
    private fun getAppVersionCode() {
        val remoteVersionCode = firebaseRemoteConfig.getString(Constants.APP_VERSION_CODE)
        val remoteVersionName = firebaseRemoteConfig.getString(Constants.APP_VERSION_NAME)
        toast("App Version Code - $remoteVersionCode\nApp Version Name $remoteVersionName")
        logD("App Version Code - $remoteVersionCode\nApp Version Name $remoteVersionName")
        try {
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            val currentVersionName = pInfo.versionName
            val currentVersionCode = pInfo.versionCode
            val longVersionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pInfo.longVersionCode
            } else {

            }

            logD("Current Version Name - $currentVersionName\n" +
                    "Current Version Code - $currentVersionCode\n" +
                    "Long Version Code - $longVersionCode")

        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }


    }

    private fun addFireBase() {
        val bundle = Bundle()
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
    }

    private fun isSignedIn(): Boolean {
        return GoogleSignIn.getLastSignedInAccount(this) != null
    }

    private fun updateFcmToken(updateFcmToken: Users.UpdateFcmToken) {
        compositeDisposable.add(userService.updateFcmToken(updateFcmToken)
                .processRequest(
                        { response ->
                            if (!response.isSuccess) {
                                putPref(SharedPrefKeys.FCM_TOKEN_ID, updateFcmToken.fcmTokenId)
                                logD("Error updating Fcm - $response.message")
                                Global.updateCrashlyticsMessage(updateFcmToken.mobileNumber!!, "Error updating Fcm data to server.", "UpdatingFcmData", Exception(response.message))
                            }
                            logD(response.message)
                        },
                        { err ->
                            Global.updateCrashlyticsMessage(updateFcmToken.mobileNumber!!, "Error updating Fcm data to server.", "UpdatingFcmData", Exception(err))
                        }
                ))
    }

    private fun updateDisplayName(updateDisplayName: Users.UpdateDisplayName) {
        if (!isNetConnected()) {
            toast("No Internet !")
            return
        }
        showProgress()
        compositeDisposable.add(userService.updateDisplayName(updateDisplayName)
                .processRequest(
                        { response ->
                            hideProgress()
                            if (response.isSuccess) {
                                putPref(SharedPrefKeys.DISPLAY_NAME, updateDisplayName.displayName)
                                val libPlayGame = LibPlayGame(this)
                                addFireBase()
                                LibGameConstants.GameConstants.mInvitationClient = Games.getInvitationsClient(this, libPlayGame.getSignInAccount()!!)
                                LibGameConstants.GameConstants.mInvitationClient?.registerInvitationCallback(libPlayGame.mInvitationCallbackHandler)
                                val dashboardFragment = DashboardFragment()
                                switchToFragment(dashboardFragment)
                            } else {
                                showDialog(this, "Error", response.message)
                            }
                        },
                        { err ->
                            hideProgress()
                            showDialog(this, "Error", err.toString())
                            Global.updateCrashlyticsMessage(updateDisplayName.mobileNumber!!, "Error on updating display name.", "Updating Display Name", Exception(err))
                        }
                ))
    }

    override fun onFragmentInteraction(uri: Uri) {

    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        when (count) {
            0 -> {
                when {
                    back_pressed + 2000 > System.currentTimeMillis() -> super.onBackPressed()
                    else -> {
                        Toast.makeText(baseContext, "Tap again to exit", Toast.LENGTH_SHORT).show()
                        back_pressed = System.currentTimeMillis()
                    }
                }
            }
            1 -> supportFragmentManager.popBackStack()
            2 -> {
                val message = "Do you want to left the room ?"
                showAlert(message)
            }
            3 -> {
                val message = "If you left the game your bid points will be deducted.\nDo you want to leave the game?"
                showAlert(message)
            }
            else -> {
                startActivity<MainActivity>()
                finish()
            }
        }
    }

    private fun showAlert(message: String) {
        AlertDialog.Builder(this).setMessage(message).setPositiveButton("Ok") { dialog, which ->
            super.onBackPressed()
            startActivity<MainActivity>()
            libPlayGame.leaveRoom()
            finish()
            dialog.dismiss()
        }.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != 0) {
            libPlayGame = LibPlayGame(this)
            libPlayGame.onActivityResult(requestCode, resultCode, data)
        } else {
            hideProgress()
        }
    }

}
