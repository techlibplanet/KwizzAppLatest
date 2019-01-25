package com.example.mayank.kwizzapp.dependency.components


import com.example.mayank.kwizzapp.dependency.scopes.ActivityScope
import dagger.Component
import kwizzapp.com.kwizzapp.MainActivity
import kwizzapp.com.kwizzapp.play.PlayActivity
import kwizzapp.com.kwizzapp.wallet.WalletActivity

@ActivityScope
@Component(dependencies = arrayOf(ApplicationComponent::class))
interface InjectActivityComponent {
    fun injectWalletActivity(walletActivity: WalletActivity)
//    fun injectSampleActivity(sampleActivity: SampleActivity)
    fun injectMainActivity(mainActivity: MainActivity)
    fun injectPlayActivity(playActivity: PlayActivity)
}
