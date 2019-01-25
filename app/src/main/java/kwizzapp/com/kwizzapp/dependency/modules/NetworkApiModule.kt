package com.example.mayank.kwizzapp.dependency.modules

import com.example.mayank.kwizzapp.dependency.scopes.ApplicationScope
import dagger.Module
import dagger.Provides
import kwizzapp.com.kwizzapp.services.IQuestion
import kwizzapp.com.kwizzapp.services.ITransaction
import kwizzapp.com.kwizzapp.services.IUser
import retrofit2.Retrofit

@Module(includes = arrayOf(HttpModule::class))
class NetworkApiModule {

    @Provides
    @ApplicationScope
    fun userService(retrofit: Retrofit): IUser {
        return retrofit.create(IUser::class.java)
    }

    @Provides
    @ApplicationScope
    fun transactionService(retrofit: Retrofit): ITransaction {
        return retrofit.create(ITransaction::class.java)
    }

    @Provides
    @ApplicationScope
    fun questionService(retrofit: Retrofit): IQuestion {
        return retrofit.create(IQuestion::class.java)
    }
}