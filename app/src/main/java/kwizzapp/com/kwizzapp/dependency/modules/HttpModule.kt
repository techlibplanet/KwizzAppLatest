package com.example.mayank.kwizzapp.dependency.modules


import com.example.mayank.kwizzapp.dependency.scopes.ApplicationScope
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import kwizzapp.com.kwizzapp.Constants
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

@Module
class HttpModule {
    @Provides
    @ApplicationScope
    fun loggingInterceptor(): okhttp3.logging.HttpLoggingInterceptor {
        val interceptor = okhttp3.logging.HttpLoggingInterceptor(okhttp3.logging.HttpLoggingInterceptor.Logger { message -> Timber.i(message) })
        interceptor.level = okhttp3.logging.HttpLoggingInterceptor.Level.BASIC
        return interceptor
    }

    @Provides
    @ApplicationScope
    fun okhttpIntersepter(): okhttp3.Interceptor {
        return okhttp3.Interceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder().addHeader("Content-Type", "application/json")
            chain.proceed(requestBuilder.build())
        }
    }

    @Provides
    @ApplicationScope
    fun okHttpClient(interceptor: okhttp3.Interceptor, loggingInterceptor: okhttp3.logging.HttpLoggingInterceptor): okhttp3.OkHttpClient {
        return okhttp3.OkHttpClient.Builder()
                .readTimeout(Constants.READ_TIMEOUT, java.util.concurrent.TimeUnit.SECONDS)
                .connectTimeout(Constants.CONNECTION_TIMEOUT, java.util.concurrent.TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .retryOnConnectionFailure(true)
                .build()
    }

    @Provides
    @ApplicationScope
    fun gson(): retrofit2.converter.gson.GsonConverterFactory = retrofit2.converter.gson.GsonConverterFactory.create(com.google.gson.GsonBuilder().setLenient().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create())

    @Provides
    @ApplicationScope
    fun rxJavaFactory(): retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory = retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory.create()

    @Provides
    @ApplicationScope
    fun retrofit(okHttpClient: okhttp3.OkHttpClient, rxJavaFactory: retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory, gson: retrofit2.converter.gson.GsonConverterFactory): retrofit2.Retrofit {
        return retrofit2.Retrofit.Builder()
                .baseUrl(Constants.API_BASE_URL)
                .addCallAdapterFactory(rxJavaFactory)
                .addConverterFactory(gson)
                .client(okHttpClient)
                .build()
    }
}