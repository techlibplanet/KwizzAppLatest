package kwizzapp.com.kwizzapp

import android.app.Application
import com.example.mayank.kwizzapp.dependency.components.ApplicationComponent
import com.example.mayank.kwizzapp.dependency.components.DaggerApplicationComponent
import com.example.mayank.kwizzapp.dependency.modules.AppContextModule
import retrofit2.Retrofit
import timber.log.Timber

class KwizzApp : Application() {

    companion object {
        lateinit var applicationComponent: ApplicationComponent
        lateinit var retrofit : Retrofit
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        applicationComponent = DaggerApplicationComponent.builder()
                .appContextModule(AppContextModule(applicationContext))
                .build()

    }
}