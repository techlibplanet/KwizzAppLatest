package kwizzapp.com.kwizzapp.viewmodels

import android.net.Uri

class ResultViewModel {

    class MultiplayerResultVm(val playerName: String, val rightAnswers: Int, val imageUri: Uri?, val wrongAnswer : Int, val dropQuestion : Int)

    class MultiplayerPointsVm(val playerName: String, val score : Int, val pointsBid: Int, val pointsLoose: Int, val pointsWin: Int, val totalPoints: Int, val imageUri: Uri?)

}