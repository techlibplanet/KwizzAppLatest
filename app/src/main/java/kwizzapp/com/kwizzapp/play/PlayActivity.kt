package kwizzapp.com.kwizzapp.play


import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import com.example.mayank.kwizzapp.libgame.LibGameConstants
import com.example.mayank.kwizzapp.libgame.LibPlayGame
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.games.Games
import com.google.android.gms.games.InvitationsClient
import kwizzapp.com.kwizzapp.R
import kwizzapp.com.kwizzapp.multiplayer.GameMenuFragment
import kwizzapp.com.kwizzapp.multiplayer.MultiplayerMenuFragment
import kwizzapp.com.kwizzapp.multiplayer.MultiplayerResultFragment
import kwizzapp.com.kwizzapp.quiz.QuizFragment
import net.rmitsolutions.mfexpert.lms.helpers.switchToFragment
import org.jetbrains.anko.find

class PlayActivity : AppCompatActivity(),
        GameMenuFragment.OnFragmentInteractionListener, MultiplayerMenuFragment.OnFragmentInteractionListener,
        QuizFragment.OnFragmentInteractionListener, MultiplayerResultFragment.OnFragmentInteractionListener{

    private lateinit var libPlayGame: LibPlayGame
    private lateinit var toolBar: Toolbar
    private var mInvitationsClient: InvitationsClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)

        toolBar = find(R.id.toolbar)
        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        libPlayGame = LibPlayGame(this)

        val gameMenuFragment = GameMenuFragment()
        switchToFragment(gameMenuFragment)

        val gameClient = Games.getGamesClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
        gameClient.setViewForPopups(find(android.R.id.content))
        // update the clients
        val libPlayGame = LibPlayGame(this)
        LibGameConstants.GameConstants.mInvitationClient = Games.getInvitationsClient(this, libPlayGame.getSignInAccount()!!)
        LibGameConstants.GameConstants.mInvitationClient?.registerInvitationCallback(libPlayGame.mInvitationCallbackHandler)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        libPlayGame = LibPlayGame(this)
        libPlayGame.onActivityResult(requestCode, resultCode, data)

    }

    override fun onFragmentInteraction(uri: Uri) {

    }
}
