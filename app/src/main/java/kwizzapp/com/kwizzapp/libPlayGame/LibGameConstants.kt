package com.example.mayank.kwizzapp.libgame

import android.net.Uri
import com.google.android.gms.games.InvitationsClient
import com.google.android.gms.games.PlayersClient
import com.google.android.gms.games.RealTimeMultiplayerClient
import com.google.android.gms.games.multiplayer.Participant
import com.google.android.gms.games.multiplayer.realtime.Room
import com.google.android.gms.games.multiplayer.realtime.RoomConfig
import kwizzapp.com.kwizzapp.viewmodels.ResultViewModel
import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet

class LibGameConstants {
    object GameConstants{
        val RC_SELECT_PLAYERS = 10000
        val RC_INVITATION_INBOX = 10001
        val RC_WAITING_ROOM = 10002
        var mInvitationClient : InvitationsClient? = null

        // Holds the configuration of the current room.
        internal var mRoomConfig: RoomConfig? = null

        // Client used to interact with the real time multiplayer system.
        var mRealTimeMultiplayerClient: RealTimeMultiplayerClient? = null

        var mRoom : Room? = null
        // Room ID where the currently active game is taking place; null if we're
        // not playing.
        var mRoomId: String? = null

        // The participants in the currently active game
        var mParticipants: ArrayList<Participant>? = null

        // Score of other participants. We update this as we receive their scores
        // from the network.
        var mParticipantScore: MutableMap<String, Int> = HashMap()

        var mParticipantWrong: MutableMap<String, Int> = HashMap()

        var mParticipantDrop: MutableMap<String, Int> = HashMap()
        // Participants who sent us their final score.
        var mFinishedParticipants: MutableSet<String> = HashSet()

        // My participant ID in the currently active game
        var mMyId: String? = null

        var mPlayerId: String? = null

        lateinit var modelList: MutableList<ResultViewModel.MultiplayerResultVm>
        var resultList :MutableList<ResultViewModel.MultiplayerResultVm>? = null
        var listResult : MutableMap<String, ResultViewModel.MultiplayerResultVm> = HashMap()
        var imageUri : Uri?= null
        var balanceAdded : Boolean = false

        var mPlayerClient : PlayersClient? = null

        var displayName : String? = null

    }

}