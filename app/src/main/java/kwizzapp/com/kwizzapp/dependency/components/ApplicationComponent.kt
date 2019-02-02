package com.example.mayank.kwizzapp.dependency.components

import android.content.Context
import com.example.mayank.kwizzapp.dependency.modules.AppContextModule
import com.example.mayank.kwizzapp.dependency.modules.DatabaseModule
import com.example.mayank.kwizzapp.dependency.modules.NetworkApiModule
import com.example.mayank.kwizzapp.dependency.qualifiers.ApplicationContextQualifier
import com.example.mayank.kwizzapp.dependency.scopes.ApplicationScope
import dagger.Component
import kwizzapp.com.kwizzapp.services.IQuestion
import kwizzapp.com.kwizzapp.services.IRazorpay
import kwizzapp.com.kwizzapp.services.ITransaction
import kwizzapp.com.kwizzapp.services.IUser
import okhttp3.OkHttpClient

@ApplicationScope
@Component(modules = arrayOf(AppContextModule::class, NetworkApiModule::class, DatabaseModule::class))
interface ApplicationComponent {

    @ApplicationContextQualifier
    fun getAppContext(): Context

    fun getOkHttpClient(): OkHttpClient
//    fun getDatabase(): MfExpertLmsDatabase

    fun getUserService(): IUser
//
    fun getTransactionService() : ITransaction

    fun getQuestionService() : IQuestion

    fun getRazorPayService() : IRazorpay
}