package com.example.mayank.kwizzapp.libgame

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.example.mayank.kwizzapp.libgame.LibGameConstants.*
import com.example.mayank.kwizzapp.libgame.LibGameConstants.GameConstants.RC_INVITATION_INBOX
import com.example.mayank.kwizzapp.libgame.LibGameConstants.GameConstants.mFinishedParticipants
import com.example.mayank.kwizzapp.libgame.LibGameConstants.GameConstants.mInvitationClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.games.*
import com.google.android.gms.games.multiplayer.Invitation
import com.google.android.gms.games.multiplayer.InvitationCallback
import com.google.android.gms.games.multiplayer.Multiplayer
import com.google.android.gms.games.multiplayer.Participant
import com.google.android.gms.games.multiplayer.realtime.*
import com.google.android.gms.tasks.OnFailureListener
import kwizzapp.com.kwizzapp.MainActivity
import kwizzapp.com.kwizzapp.R
import kwizzapp.com.kwizzapp.multiplayer.MultiplayerMenuFragment
import kwizzapp.com.kwizzapp.multiplayer.MultiplayerResultFragment
import kwizzapp.com.kwizzapp.viewmodels.ResultViewModel
import net.rmitsolutions.mfexpert.lms.helpers.*
import org.jetbrains.anko.find



class LibPlayGame(private var activity: Activity) {

    private val TAG = LibPlayGame::class.java.simpleName

    init {
        GameConstants.resultList = mutableListOf<ResultViewModel.MultiplayerResultVm>()
        LibGameConstants.GameConstants.mRealTimeMultiplayerClient = getRealTimeMultiPlayerClient()
        GameConstants.mPlayerClient = getPlayerClient()
        GameConstants.displayName = getDisplayName()
        GameConstants.mPlayerId = getPlayerId()
        GameConstants.mMyId = getMyId()
        GameConstants.modelList = mutableListOf<ResultViewModel.MultiplayerResultVm>()
        GameConstants.mInvitationClient = Games.getInvitationsClient(activity, getSignInAccount()!!)
        GameConstants.imageUri = getImageUri()

        GameConstants.mInvitationClient = getInvitationClient()


    }

    fun getSignInAccount(): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(activity)
    }

    fun getRealTimeMultiPlayerClient(): RealTimeMultiplayerClient {
        return Games.getRealTimeMultiplayerClient(activity, getSignInAccount()!!)
    }

    fun getPlayerId(): String? {
        GameConstants.mPlayerClient?.currentPlayer?.addOnSuccessListener { player -> GameConstants.mPlayerId = player.playerId }
        return GameConstants.mPlayerId
    }

    fun getPlayerClient(): PlayersClient? {
        return Games.getPlayersClient(activity, getSignInAccount()!!)
    }

    fun getMyId(): String? {
        if (GameConstants.mMyId != null) {
            return GameConstants.mMyId
        }
        return null
    }

    fun getImageUri(): Uri? {
        GameConstants.mPlayerClient?.currentPlayer?.addOnSuccessListener { player -> GameConstants.imageUri = player.iconImageUri }
        return GameConstants.imageUri
    }

    fun getInvitationClient(): InvitationsClient? {
        return Games.getInvitationsClient(activity, GoogleSignIn.getLastSignedInAccount(activity)!!)
    }

    private fun getDisplayName(): String? {
        val displayName = activity.getPref(SharedPrefKeys.DISPLAY_NAME, "")
        if (displayName != "") {
            return displayName
        }
        return null
    }

    fun showInvitationInbox() {
        activity.hideProgress()
        mInvitationClient?.invitationInboxIntent
                ?.addOnSuccessListener { intent -> activity.startActivityForResult(intent, RC_INVITATION_INBOX) }
    }

    fun invitePlayers() {
        Log.d(TAG, "Multiplayer clicked")
        GameConstants.mRealTimeMultiplayerClient?.getSelectOpponentsIntent(1, 7)?.addOnSuccessListener { intent ->
            activity.startActivityForResult(intent, GameConstants.RC_SELECT_PLAYERS)

        }?.addOnFailureListener(createFailureListener("There was a problem selecting opponents."))
    }

    fun startQuickGame() {
        // quick-start a game with 1 randomly selected opponent
        Log.d(TAG, "Starting Quick Game")
        val MIN_OPPONENTS = 1
        val MAX_OPPONENTS = 1
        val autoMatchCriteria = RoomConfig.createAutoMatchCriteria(MIN_OPPONENTS, MAX_OPPONENTS, 0)
        keepScreenOn()
        GameConstants.mRoomConfig = RoomConfig.builder(mRoomUpdateCallback)
                .setOnMessageReceivedListener(mOnRealTimeMessageReceivedListener)
                .setRoomStatusUpdateCallback(mRoomStatusUpdateCallback)
                .setAutoMatchCriteria(autoMatchCriteria)
                .build()
        GameConstants.mRealTimeMultiplayerClient?.create(GameConstants.mRoomConfig!!)
    }

    private fun keepScreenOn() {
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    // Clears the flag that keeps the screen on.
    private fun stopKeepingScreenOn() {
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    val mRoomUpdateCallback = object : RoomUpdateCallback() {
        // Called when room has been created
        override fun onRoomCreated(statusCode: Int, room: Room?) {
            Log.d(TAG, "onRoomCreated($statusCode, $room)")
            if (statusCode != GamesCallbackStatusCodes.OK) {
                Log.e(TAG, "*** Error: onRoomCreated, status $statusCode")
                showGameError()
                // Get the player Id here
                GameConstants.mRoom = room
                return
            }

            // save room ID so we can leave cleanly before the game starts.
            GameConstants.mRoomId = room!!.roomId

            // show the waiting room UI
            showWaitingRoom(room)
        }

        // Called when room is fully connected.
        override fun onRoomConnected(statusCode: Int, room: Room?) {
            Log.d(TAG, "onRoomConnected($statusCode, $room)")
            if (statusCode != GamesCallbackStatusCodes.OK) {
                Log.e(TAG, "*** Error: onRoomConnected, status $statusCode")
                showGameError()
                return
            }
            updateRoom(room)
        }

        override fun onJoinedRoom(statusCode: Int, room: Room?) {
            Log.d(TAG, "onJoinedRoom($statusCode, $room)")
            if (statusCode != GamesCallbackStatusCodes.OK) {
                Log.e(TAG, "*** Error: onRoomConnected, status $statusCode")
                showGameError()
                return
            }

            // show the waiting room UI
            showWaitingRoom(room!!)
        }

        // Called when we've successfully left the room (this happens a result of voluntarily leaving
        // via a call to leaveRoom(). If we get disconnected, we get onDisconnectedFromRoom()).
        override fun onLeftRoom(statusCode: Int, roomId: String) {
            // we have left the room; return to main screen.
            Log.d(TAG, "onLeftRoom, code $statusCode")
            activity.hideProgress()
//            switchToMainScreen()
        }
    }

    private var mOnRealTimeMessageReceivedListener: OnRealTimeMessageReceivedListener = OnRealTimeMessageReceivedListener { realTimeMessage ->
        val buf = realTimeMessage.messageData
        val sender = realTimeMessage.senderParticipantId
        Log.d(TAG, "Sender : $sender")


        val state = buf[0].toChar()
        val value1 = buf[1].toInt()
        if (state == 'R') {
            val value2 = buf[2].toInt()
            val value3 = buf[3].toInt()
            Log.d(TAG, "Message received: " + buf[0].toChar() + "/" + buf[1].toInt() + "/" + buf[2].toInt() + "/" + buf[3].toInt())
            GameConstants.mParticipantScore.put(sender, value1)
            GameConstants.mParticipantWrong.put(sender, value2)
            GameConstants.mParticipantDrop.put(sender, value3)
            if (GameConstants.mFinishedParticipants.contains(sender)) {
                Log.d(TAG, "Participants already added")
            } else {
                GameConstants.mFinishedParticipants.add(sender)
                Log.d(TAG, "Adding sender in realtime")
                sendBroadcastResult(state, value1, value2, value3)
            }
        }

        if (state == 'A' || state == 'S') {
            Log.d(TAG, "Message received: " + buf[0].toChar() + "/" + buf[1].toInt())
            sendBroadcastMessage(state, value1)
        }

    }

    private val mRoomStatusUpdateCallback = object : RoomStatusUpdateCallback() {
        // Called when we are connected to the room. We're not ready to play yet! (maybe not everybody
        // is connected yet).
        override fun onConnectedToRoom(room: Room?) {
            Log.d(TAG, "onConnectedToRoom.")

            //get participants and my ID:
            GameConstants.mParticipants = room?.participants!!
            GameConstants.mMyId = room.getParticipantId(GameConstants.mPlayerId)

            // save room ID if its not initialized in onRoomCreated() so we can leave cleanly before the game starts.
            if (GameConstants.mRoomId == null) {
                GameConstants.mRoomId = room.roomId
            }

            // print out the list of participants (for debug purposes)
            Log.d(TAG, "Room ID: ${GameConstants.mRoomId}")
            Log.d(TAG, "My ID ${GameConstants.mMyId}")
            Log.d(TAG, "<< CONNECTED TO ROOM>>")
            val multiplayerMenu = MultiplayerMenuFragment()
            activity.switchToFragmentBackStack(multiplayerMenu)
            activity.hideProgress()


        }

        // Called when we get disconnected from the room. We return to the main screen.
        override fun onDisconnectedFromRoom(room: Room?) {
            Log.d(TAG, "On disconnected from room")
            GameConstants.mRoomId = null
            GameConstants.mRoomConfig = null
            if (mFinishedParticipants.size != room?.participants?.size) {
                AlertDialog.Builder(activity).setTitle("Disconnected").setMessage("All players left the game").setCancelable(false)
                        .setPositiveButton("Ok") { dialog, which ->
                            clearData()
                            val intent = Intent(activity, MainActivity::class.java)
                            activity.startActivity(intent)
                            activity.finish()
                            dialog.dismiss()
                        }.show()
            }
        }


        // We treat most of the room update callbacks in the same way: we update our list of
        // participants and update the display. In a real game we would also have to check if that
        // change requires some action like removing the corresponding player avatar from the screen,
        // etc.
        override fun onPeerDeclined(room: Room?, arg1: List<String>) {
            updateRoom(room)
        }

        override fun onPeerInvitedToRoom(room: Room?, arg1: List<String>) {
            updateRoom(room)
        }

        override fun onP2PDisconnected(participant: String) {}

        override fun onP2PConnected(participant: String) {}

        override fun onPeerJoined(room: Room?, arg1: List<String>) {
            updateRoom(room)
        }

        override fun onPeerLeft(room: Room?, peersWhoLeft: List<String>) {
            updateRoom(room)
        }

        override fun onRoomAutoMatching(room: Room?) {
            updateRoom(room)
        }

        override fun onRoomConnecting(room: Room?) {
            updateRoom(room)
        }

        override fun onPeersConnected(room: Room?, peers: List<String>) {
            updateRoom(room)
        }

        override fun onPeersDisconnected(room: Room?, peers: List<String>) {
            updateRoom(room)
        }
    }

    private fun showGameError() {
        activity.showDialog(activity, "Error", activity.getString(R.string.game_problem))
    }

    private fun showWaitingRoom(room: Room) {
        activity.hideProgress()
        val MIN_PLAYERS = Integer.MAX_VALUE
        GameConstants.mRealTimeMultiplayerClient?.getWaitingRoomIntent(room, MIN_PLAYERS)
                ?.addOnSuccessListener { intent ->
                    // show waiting room UI
                    activity.startActivityForResult(intent, GameConstants.RC_WAITING_ROOM)
                }
                ?.addOnFailureListener(createFailureListener("There was a problem getting the waiting room!"))
    }

    fun createFailureListener(string: String): OnFailureListener {
        return OnFailureListener { e -> handleException(e, string) }
    }

    private fun handleException(exception: Exception?, details: String) {
        var status = 0

        if (exception is ApiException) {
            val apiException = exception as ApiException?
            status = apiException!!.statusCode
        }

        var errorString: String? = null
        when (status) {
            GamesCallbackStatusCodes.OK -> {
            }
            GamesClientStatusCodes.MULTIPLAYER_ERROR_NOT_TRUSTED_TESTER -> errorString = activity.getString(R.string.status_multiplayer_error_not_trusted_tester)
            GamesClientStatusCodes.MATCH_ERROR_ALREADY_REMATCHED -> errorString = activity.getString(R.string.match_error_already_rematched)
            GamesClientStatusCodes.NETWORK_ERROR_OPERATION_FAILED -> errorString = activity.getString(R.string.network_error_operation_failed)
            GamesClientStatusCodes.INTERNAL_ERROR -> errorString = activity.getString(R.string.internal_error)
            GamesClientStatusCodes.MATCH_ERROR_INACTIVE_MATCH -> errorString = activity.getString(R.string.match_error_inactive_match)
            GamesClientStatusCodes.MATCH_ERROR_LOCALLY_MODIFIED -> errorString = activity.getString(R.string.match_error_locally_modified)
            else -> errorString = activity.getString(R.string.unexpected_status, GamesClientStatusCodes.getStatusCodeString(status))
        }
        if (errorString == null) {
            return
        }
        val message = activity.getString(R.string.status_exception_error, details, status, exception)
        activity.showDialog(activity, "Error", message + "\n" + errorString)
    }

    private fun updateRoom(room: Room?) {
        if (room != null) {
            GameConstants.mParticipants = room.participants
        }
        if (GameConstants.mParticipants != null) {
            Log.d(TAG, "Update peer score display..")
        }
    }


    private fun sendBroadcastResult(state: Char, value1: Int, value2: Int, value3: Int) {
        Log.d(TAG, "Sending broadcast")
        activity.sendBroadcast(Intent(MultiplayerResultFragment.ACTION_RESULT_RECEIVED)
                .putExtra("state", state)
                .putExtra("RightAnswers", value1)
                .putExtra("WrongAnswers", value2)
                .putExtra("DropQuestions", value3))
    }

    private fun sendBroadcastMessage(state: Char, value: Int) {
        activity.sendBroadcast(Intent(MultiplayerMenuFragment.ACTION_MESSAGE_RECEIVED)
                .putExtra("state", state)
                .putExtra("value", value));
    }

    fun clearData() {
        GameConstants.mRealTimeMultiplayerClient = null
        GameConstants.mInvitationClient = null
        GameConstants.mRoomId = null
        GameConstants.mRoomConfig = null
        GameConstants.mRoom = null
        GameConstants.mFinishedParticipants.clear()
        GameConstants.mParticipantScore.clear()
        GameConstants.mParticipants?.clear()
        GameConstants.modelList.clear()
        GameConstants.balanceAdded = false
        GameConstants.mMyId = null
        GameConstants.mPlayerId = null
        GameConstants.mPlayerClient = null
        GameConstants.resultList?.clear()
        GameConstants.listResult.clear()

    }

    val mInvitationCallbackHandler = object : InvitationCallback() {
        override fun onInvitationRemoved(invitationId: String) {
            Log.d(TAG, "Invitation removed - $invitationId")
        }

        override fun onInvitationReceived(invitation: Invitation) {
            Log.d(TAG, "On invitation received called...")
            val inviter = invitation.inviter
            val name = inviter.displayName
            Log.d(TAG, "Inviter Name - $name Inviter - $inviter")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
                dialog(activity, "Invitation Received", "${invitation.inviter.displayName} is challenging you to a game!", invitation.invitationId)
            } else {
                AlertDialog.Builder(activity).setTitle("Invitation Received").setMessage("${invitation.inviter.displayName} is challenging you to a game!")
                        .setPositiveButton("Accept") { dialog, which ->
                            activity.showProgress()
                            acceptInviteToRoom(invitation.invitationId)
                            dialog?.dismiss()
                        }.setNegativeButton("Reject") { dialog, which ->
                            declineInvitation(invitation.invitationId)
                            dialog?.dismiss()
                        }.show()
            }

            keepScreenOn()
        }

    }


    private var dialog: Dialog? = null

    fun dialog(activity: Activity, title: String, message: String, invitationId: String) {
        dialog = Dialog(activity)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(false)
        dialog?.setContentView(R.layout.invitation_alert_dialog)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val titleTextView = dialog?.findViewById<TextView>(R.id.textViewTitle)
        val messageTextView = dialog?.findViewById<TextView>(R.id.textViewMessage)

        titleTextView?.text = title
        messageTextView?.text = message

        val accept = dialog?.find<Button>(R.id.buttonAccept)
        val reject = dialog?.find<Button>(R.id.buttonReject)


        accept?.setOnClickListener {
            activity.showProgress()
            acceptInviteToRoom(invitationId)
            dialog?.dismiss()
        }
        reject?.setOnClickListener {
            declineInvitation(invitationId)
            dialog?.dismiss()
        }

        dialog?.show()
        dialog?.window?.setLayout(800, 600)
    }


    private fun acceptInviteToRoom(invitationId: String?) {
        Log.d(TAG, "Accepting invitation: $invitationId")
        GameConstants.mRoomConfig = RoomConfig.builder(mRoomUpdateCallback)
                .setInvitationIdToAccept(invitationId)
                .setOnMessageReceivedListener(mOnRealTimeMessageReceivedListener)
                .setRoomStatusUpdateCallback(mRoomStatusUpdateCallback).build()
        keepScreenOn()
        Games.getRealTimeMultiplayerClient(activity, GoogleSignIn.getLastSignedInAccount(activity)!!).join(GameConstants.mRoomConfig!!)
    }

    private fun declineInvitation(invitationId: String) {
        GameConstants.mRoomConfig = RoomConfig.builder(mRoomUpdateCallback)
                .setInvitationIdToAccept(invitationId)
                .setOnMessageReceivedListener(mOnRealTimeMessageReceivedListener)
                .setRoomStatusUpdateCallback(mRoomStatusUpdateCallback).build()
        stopKeepingScreenOn()
        Games.getRealTimeMultiplayerClient(activity, GoogleSignIn.getLastSignedInAccount(activity)!!).declineInvitation(invitationId)

    }

    fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (requestCode == GameConstants.RC_SELECT_PLAYERS) {
            // we got the result from the "select players" UI -- ready to create the room
            when (resultCode) {
                Activity.RESULT_CANCELED -> activity.hideProgress()
                Activity.RESULT_OK -> {
                    if (intent != null) {
                        handleSelectPlayersResult(resultCode, intent)
                    } else {
                        activity.hideProgress()
                        activity.showDialog(activity, "Error", "Some error occurred please try again...")
                    }
                }
            }

        } else if (requestCode == GameConstants.RC_WAITING_ROOM) {
            // we got the result from the "waiting room" UI.
            when (resultCode) {
                Activity.RESULT_OK -> // ready to start playing
                    Log.d(TAG, "Starting game (waiting room returned OK).")
                //startGame(true)
                GamesActivityResultCodes.RESULT_LEFT_ROOM -> // player indicated that they want to leave the room
                    leaveRoom()
                Activity.RESULT_CANCELED -> // Dialog was cancelled (user pressed back key, for instance). In our game,
                    // this means leaving the room too. In more elaborate games, this could mean
                    // something else (like minimizing the waiting room UI).
                    leaveRoom()
            }
        } else if (requestCode == GameConstants.RC_INVITATION_INBOX) {

            activity.hideProgress()
            if (resultCode != Activity.RESULT_OK) {
                // Canceled or some error.
                return;
            }

            val invitation = intent?.extras?.getParcelable<Invitation>(Multiplayer.EXTRA_INVITATION)
            if (invitation != null) {
//                val builder = RoomConfig.builder(mRoomUpdateCallback).setInvitationIdToAccept(invitation.invitationId)
//                mJoinedRoomConfig = builder.build()
//                Games.getRealTimeMultiplayerClient(thisActivity, GoogleSignIn.getLastSignedInAccount(thisActivity)!!)
//                        .join(mJoinedRoomConfig!!)
//                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                acceptInviteToRoom(invitation.invitationId)
            }

        }

    }

    private fun handleSelectPlayersResult(response: Int, data: Intent) {
        Log.d(TAG, "Inside select player result")
        if (response != Activity.RESULT_OK) {
            Log.w(TAG, "*** select players UI cancelled, $response")
        }
        Log.d(TAG, "Select players UI succeeded.")

        // get the invitee list
        val invitees = data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS)
        Log.d(TAG, "Invitee count: " + invitees.size)

        // get the automatch criteria
        var autoMatchCriteria: Bundle? = null
        val minAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0)
        val maxAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0)
        if (minAutoMatchPlayers > 0 || maxAutoMatchPlayers > 0) {
            autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
                    minAutoMatchPlayers, maxAutoMatchPlayers, 0)
            Log.d(TAG, "Automatch criteria: " + autoMatchCriteria!!)
        }

        // create the room
        Log.d(TAG, "Creating room...")
        keepScreenOn()

        GameConstants.mRoomConfig = RoomConfig.builder(mRoomUpdateCallback)
                .addPlayersToInvite(invitees)
                .setOnMessageReceivedListener(mOnRealTimeMessageReceivedListener)
                .setRoomStatusUpdateCallback(mRoomStatusUpdateCallback)
                .setAutoMatchCriteria(autoMatchCriteria).build()
        GameConstants.mRealTimeMultiplayerClient?.create(GameConstants.mRoomConfig!!)
        Log.d(TAG, "Room created, waiting for it to be ready...")


    }

    // Leave the room.
    fun leaveRoom() {
        Log.d(TAG, "Leaving room.")
        //mSecondsLeft = 0
        stopKeepingScreenOn()
        activity.hideProgress()
        if (GameConstants.mRoomId != null) {
            GameConstants.mRealTimeMultiplayerClient?.leave(GameConstants.mRoomConfig!!, GameConstants.mRoomId!!)
                    ?.addOnCompleteListener {
                        GameConstants.mRoomId = null
                        GameConstants.mRoomConfig = null
                    }
            Log.d(TAG, "Room left successfully")
            clearData()
            val intent = Intent(activity, MainActivity::class.java)
            activity.startActivity(intent)
            activity.finish()

        } else {
            Log.d(TAG, "Room is null")
            val intent = Intent(activity, MainActivity::class.java)
            activity.startActivity(intent)
            clearData()
            activity.finish()
        }
    }


    private var mMsgBuffer = ByteArray(2)
    fun broadcastMessage(state: Char, score: Int) {
        // First byte in message indicates whether its Right, Wrong or Drop Question.
        mMsgBuffer[0] = state.toByte()
        // Second byte is the score
        mMsgBuffer[1] = score.toByte()


        Log.d(TAG, "List size - ${GameConstants.mParticipants?.size}")
        for (p in GameConstants.mParticipants!!) {
            if (p.participantId == GameConstants.mMyId) {
                continue
            }
            if (p.status != Participant.STATUS_JOINED) {
                continue
            }

            // Sending button inputs to all the participants and changing the UI
            sendReliableMessage(p, mMsgBuffer)
        }

    }

    private fun sendReliableMessage(p: Participant, mMsgBuffer: ByteArray) {
        GameConstants.mRealTimeMultiplayerClient?.sendReliableMessage(mMsgBuffer,
                GameConstants.mRoomId!!, p.participantId, RealTimeMultiplayerClient.ReliableMessageSentCallback { statusCode, tokenId, recipientParticipantId ->
        })
                ?.addOnSuccessListener { tokenId -> Log.d(TAG, "Created a reliable message with tokenId: " + tokenId!!) }
    }

    private var mResultBuf = ByteArray(5)

    fun broadcastResult(state: Char, right: Int, wrong: Int, drop: Int) {
        mResultBuf[0] = state.toByte()
        mResultBuf[1] = right.toByte()
        mResultBuf[2] = wrong.toByte()
        mResultBuf[3] = drop.toByte()

        for (p in GameConstants.mParticipants!!) {
            if (p.participantId == GameConstants.mMyId) {
                continue
            }
            if (p.status != Participant.STATUS_JOINED) {
                continue
            }

            // Sending result to all the participants
            sendReliableMessage(p, mResultBuf)
        }
    }

    // Switch UI to the given fragment


}