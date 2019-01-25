package kwizzapp.com.kwizzapp.models

import android.databinding.BaseObservable
import android.databinding.ObservableField

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
}