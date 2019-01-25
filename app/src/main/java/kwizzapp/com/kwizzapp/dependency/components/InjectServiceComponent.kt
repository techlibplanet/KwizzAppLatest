package com.example.mayank.kwizzapp.dependency.components

import com.example.mayank.kwizzapp.dependency.scopes.ActivityScope
import dagger.Component
import kwizzapp.com.kwizzapp.fcm.MyFirebaseMessagingService


@ActivityScope
@Component(dependencies = arrayOf(ApplicationComponent::class))
interface InjectServiceComponent {
    fun injectMyFirebaseMessagingService(myFirebaseMessagingService: MyFirebaseMessagingService)
}