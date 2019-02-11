package kwizzapp.com.kwizzapp.models

import android.databinding.BaseObservable
import android.databinding.ObservableField
import kwizzapp.com.kwizzapp.viewmodels.CommonResult

class Users {

    class InsertUserInfo{
        var firstName :String? = null
        var lastName : String? = null
        var displayName :  String? = null
        var mobileNumber : String? = null
        var email :String? = null
        var playerId: String? = null
        var fcmTokenId : String? =null
        var firebaseInstanceId :  String? = null
        var timeStamp : String? = null
    }

    class UpdateUserInfo{
        var firstName :String? = null
        var lastName : String? = null
        var mobileNumber : String? = null
        var email :String? = null
    }


    class UpdateDisplayName{
        var mobileNumber : String? = null
        var displayName: String? = null
    }

    class InsertFcmData{
        var mobileNumber : String? = null
        var displayName :  String? = null
        var email : String? = null
        var fcmTokenId : String? = null
    }

    class UpdateFcmToken{
        var mobileNumber : String? = null
        var fcmTokenId :  String? = null
        var firebaseInstanceId : String? = null
    }

    class Profile{
        var playerId : String? = null
    }

    class ProfileData(val firstName : String, val lastName : String, val mobileNumber : String, val email : String, val displayName : String,  val totalWin : Double, val totalLoose : Double, val balance : Double) : CommonResult()
}