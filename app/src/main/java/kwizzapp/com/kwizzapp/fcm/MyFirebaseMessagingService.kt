package kwizzapp.com.kwizzapp.fcm


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v4.app.NotificationCompat
import com.example.mayank.kwizzapp.dependency.components.DaggerInjectServiceComponent
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace
import io.reactivex.disposables.CompositeDisposable
import kwizzapp.com.kwizzapp.KwizzApp
import kwizzapp.com.kwizzapp.R
import kwizzapp.com.kwizzapp.helper.Global
import kwizzapp.com.kwizzapp.helper.processRequest
import kwizzapp.com.kwizzapp.models.Users
import kwizzapp.com.kwizzapp.services.IUser
import net.rmitsolutions.mfexpert.lms.helpers.SharedPrefKeys
import net.rmitsolutions.mfexpert.lms.helpers.getPref
import net.rmitsolutions.mfexpert.lms.helpers.putPref
import java.lang.Exception
import java.util.*
import javax.inject.Inject


class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = MyFirebaseMessagingService::class.java.simpleName

    private lateinit var compositeDisposable: CompositeDisposable
    @Inject
    lateinit var userService: IUser
    private lateinit var myTrace: Trace
    override fun onNewToken(token: String?) {
        val depComponent = DaggerInjectServiceComponent.builder()
                .applicationComponent(KwizzApp.applicationComponent)
                .build()
        depComponent.injectMyFirebaseMessagingService(this)

        compositeDisposable = CompositeDisposable()

        myTrace = FirebasePerformance.getInstance().newTrace("fcm_token_trace")
        myTrace.start()

        val mobileNumber = getPref(SharedPrefKeys.MOBILE_NUMBER, "")
        if (mobileNumber != "") {
            val fcmToken = getPref(SharedPrefKeys.FCM_TOKEN_ID, "")
            if (token != fcmToken){
                val updateFcmToken = Users.UpdateFcmToken()
                updateFcmToken.mobileNumber = mobileNumber
                updateFcmToken.fcmTokenId = token
                updateFcmToken.firebaseInstanceId = FirebaseInstanceId.getInstance().id
                sendFcmTokenToServer(updateFcmToken)
            }
        }
    }

    private fun sendFcmTokenToServer(updateFcmToken: Users.UpdateFcmToken) {
        compositeDisposable.add(userService.updateFcmToken(updateFcmToken)
                .processRequest(
                        { response ->
                            if (response.isSuccess) {
                                //Log.d("FCMToken", "FCM token updated successfully")
                                putPref(SharedPrefKeys.FCM_TOKEN_ID, updateFcmToken.fcmTokenId)
                                myTrace.incrementMetric("fcm_token_refresh_success", 1)
                            } else {
                                //Log.d("FCMToken", response.message)
                                myTrace.incrementMetric("fcm_token_refresh_failed", 1)
                                Global.updateCrashlyticsMessage(updateFcmToken.mobileNumber!!, "Error on updating FCM Token to server", "Post FCM Token", Exception(response.message))
                            }
                        },
                        { err ->
                            //Log.d("FCMToken", err)
                            myTrace.incrementMetric("fcm_token_refresh_failed", 1)
                            Global.updateCrashlyticsMessage(updateFcmToken.mobileNumber!!, "Error on updating FCM Token to server", "Post FCM Token", Exception(err))
                        }
                ))
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        //Log.d(TAG, "Remote Message From ${remoteMessage?.from}")

        remoteMessage?.data?.isNotEmpty()?.let {
            //Log.d(TAG, "Message data payload: " + remoteMessage.data)

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                //scheduleJob()
            } else {
                // Handle message within 10 seconds
                //handleNow()
            }
        }

        // Check if message contains a notification payload.
        remoteMessage?.notification?.let { notification ->
            //Log.d(TAG, "Message Notification Body: ${notification.body}")
            showNotification(notification.title, notification.body)
        }


    }

    private fun showNotification(title: String?, body: String?) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val NOTIFICATION_CHANNEL_ID = "com.kwizzapp"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, "KwizzApp", NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.description = "KwizzApp"
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = R.color.colorAccent
            notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setContentInfo("Info")
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_app_launcher))

        notificationManager.notify(Random().nextInt(), notificationBuilder.build())

    }

    override fun onMessageSent(p0: String?) {

    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}