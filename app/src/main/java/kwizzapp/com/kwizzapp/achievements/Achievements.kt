package com.technoholicdeveloper.kwizzapp.achievements

import android.app.Activity
import android.os.Bundle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.games.Games
import com.google.firebase.analytics.FirebaseAnalytics
import kwizzapp.com.kwizzapp.Constants.firebaseAnalytics
import kwizzapp.com.kwizzapp.R
import net.rmitsolutions.mfexpert.lms.helpers.SharedPrefKeys
import net.rmitsolutions.mfexpert.lms.helpers.getPref
import net.rmitsolutions.mfexpert.lms.helpers.putPref

class Achievements(private val activity: Activity) {

    private fun unlockAchievement(achievementId: Int){
        Games.getAchievementsClient(activity, GoogleSignIn.getLastSignedInAccount(activity)!!)
                .unlock(activity.getString(achievementId));
        addToFirebase(achievementId)
    }

    private fun unlockIncrementalAchievement(achievementId: Int, numberOfSteps : Int){
        Games.getAchievementsClient(activity, GoogleSignIn.getLastSignedInAccount(activity)!!)
                .increment(activity.getString(achievementId), numberOfSteps);
        addToFirebase(achievementId)
    }

    fun checkAchievements(score : Int, participants : Int, win : Boolean, trueQues: Int){
        val completeAGame = activity.getPref(SharedPrefKeys.COMPLETE_A_GAME, "")
        val winThreeGame = activity.getPref(SharedPrefKeys.WIN_THREE_GAME, "")
        if (win && score>5 && completeAGame!=""){
            unlockAchievement(R.string.achievement_kwizz_fortune)
        }

        if (completeAGame!="" && score >5){
            unlockAchievement(R.string.achievement_kwizz_novice)
            activity.putPref(SharedPrefKeys.COMPLETE_A_GAME, "Complete a game successfully !")
        }



        if (score >15){
            unlockAchievement(R.string.achievement_kwizz_beginner)
        }

        if (participants == 8 && score>5){
            unlockAchievement(R.string.achievement_kwizz_full_league)
        }

        if (winThreeGame!=""){
            unlockIncrementalAchievement(R.string.achievement_on_a_roll, winThreeGame.toInt())
        }

        if (trueQues >= 10){
            unlockAchievement(R.string.achievement_great_luck)
        }

        if (winThreeGame!=""){
            unlockIncrementalAchievement(R.string.achievement_10_rounds_back_to_back, winThreeGame.toInt())
        }

    }

    fun checkSinglePlayerAchievements(score : Int){
        val completeAGame = activity.getPref(SharedPrefKeys.COMPLETE_A_GAME, "")

        if (score >5){
            unlockAchievement(R.string.achievement_kwizz_lone_wolf)
        }

        if (completeAGame!="" && score >5){
            unlockAchievement(R.string.achievement_kwizz_novice)
            activity.putPref(SharedPrefKeys.COMPLETE_A_GAME, "Complete a game successfully !")
        }
    }

    private fun addToFirebase(achievementId: Int) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ACHIEVEMENT_ID, activity.getString(achievementId));
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.UNLOCK_ACHIEVEMENT, bundle)
    }

}