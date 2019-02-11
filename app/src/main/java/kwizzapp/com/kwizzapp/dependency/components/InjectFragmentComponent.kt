package com.example.mayank.kwizzapp.dependency.components

import com.example.mayank.kwizzapp.dependency.scopes.ActivityScope
import com.example.mayank.kwizzapp.wallet.AddPointsFragment
import com.example.mayank.kwizzapp.wallet.TransferPointsFragment
import com.example.mayank.kwizzapp.wallet.WalletMenuFragment
import com.example.mayank.kwizzapp.wallet.WithdrawalPointsFragment
import dagger.Component
import kwizzapp.com.kwizzapp.multiplayer.GameMenuFragment
import kwizzapp.com.kwizzapp.multiplayer.MultiplayerMenuFragment
import kwizzapp.com.kwizzapp.multiplayer.MultiplayerResultFragment
import kwizzapp.com.kwizzapp.quiz.QuizFragment
import kwizzapp.com.kwizzapp.settings.profilesettings.EditProfileFragment
import kwizzapp.com.kwizzapp.settings.profilesettings.ProfileFragment
import kwizzapp.com.kwizzapp.singleplay.SinglePlayQuizFragment
import kwizzapp.com.kwizzapp.userInfo.UserInfoFragment
import kwizzapp.com.kwizzapp.wallet.TransactionFragment

@ActivityScope
@Component(dependencies = arrayOf(ApplicationComponent::class))
interface InjectFragmentComponent {
    fun injectUserInfoFragment(userInfoFragment: UserInfoFragment)
    fun injectGameMenuFragment(gameMenuFragment: GameMenuFragment)
    fun injectMultiplayerMenuFragment(multiplayerMenuFragment: MultiplayerMenuFragment)
    fun injectQuizFragment(quizFragment: QuizFragment)
    fun injectMultiplayerResultFragment(multiplayerResultFragment: MultiplayerResultFragment)
    fun injectWalletMenuFragment(walletMenuFragment: WalletMenuFragment)
    fun injectAddPointsFragment(addPointsFragment: AddPointsFragment)
    fun injectWithdrawalPointsFragment(withdrawalPointsFragment: WithdrawalPointsFragment)
    fun injectTransferPointsFragment(transferPointsFragment: TransferPointsFragment)
    fun injectSinglePlayQuizFragment(singlePlayQuizFragment: SinglePlayQuizFragment)
    fun injectEditProfileFragment(editProfileFragment: EditProfileFragment)
    fun injectTransactionFragment(transactionFragment: TransactionFragment)
    fun injectProfileFragment(profileFragment: ProfileFragment)
}