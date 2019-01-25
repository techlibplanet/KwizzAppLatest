package com.example.mayank.kwizzapp.dependency.modules

import android.arch.persistence.room.Room
import android.content.Context
import com.example.mayank.kwizzapp.dependency.qualifiers.ApplicationContextQualifier
import com.example.mayank.kwizzapp.dependency.scopes.ApplicationScope
import dagger.Module
import dagger.Provides

@Module(includes = arrayOf(AppContextModule::class))
class DatabaseModule {
//    @Provides
//    @ApplicationScope
//    fun mfExpertLmsDatabase(@ApplicationContextQualifier context: Context): MfExpertLmsDatabase {
//        return Room.databaseBuilder(context, MfExpertLmsDatabase::class.java, "mfexpert-lms-db")
//                .fallbackToDestructiveMigration()
//                .allowMainThreadQueries()
//                .build()
//    }
}