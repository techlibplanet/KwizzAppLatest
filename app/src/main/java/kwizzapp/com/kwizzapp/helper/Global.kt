package kwizzapp.com.kwizzapp.helper

import android.annotation.SuppressLint
import android.app.Activity
import com.crashlytics.android.Crashlytics
import com.google.android.gms.tasks.Task
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import net.rmitsolutions.mfexpert.lms.helpers.isNetConnected
import org.jetbrains.anko.toast
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class Global {

    companion object {
        const val DISPLAY_FULL_DATE_FORMAT = "dd-MM-yyyy hh:mm:ss a"

        @SuppressLint("SimpleDateFormat")
        fun getFormatDate(date : Date): String? {
            val formatter : java.text.DateFormat = SimpleDateFormat(DISPLAY_FULL_DATE_FORMAT)
            return formatter.format(date)
        }

        fun checkInternet(activity : Activity): Boolean {
            if(!activity.isNetConnected()){
                activity.toast("No Internet !")
                return false
            }
            return true
        }

        fun doubleValueFormat(d : Double): String {
            return String.format("%.2f", d)
        }

        fun updateCrashlyticsMessage(userIdentifier : String, message : String,action : String,exception : Exception){
            Crashlytics.setUserIdentifier(userIdentifier)
            Crashlytics.setString("LastAction", action)
            Crashlytics.log(message)
            Crashlytics.logException(exception)
        }

        fun retrieveCurrentFcmToken(): Task<InstanceIdResult> {
            return FirebaseInstanceId.getInstance().instanceId
        }

        fun getDateFormat(date : Date): String? {
            val formatter : java.text.DateFormat = SimpleDateFormat(DISPLAY_FULL_DATE_FORMAT)
            return formatter.format(date)
        }

        fun isValidPhone(phone: String): Boolean {
            var check = false
            check = if (!Pattern.matches("[a-zA-Z]+", phone)) {
                !(phone.length <= 9 || phone.length > 10)
            } else {
                false
            }
            return check
        }


    }
}