package kwizzapp.com.kwizzapp.viewmodels

import android.databinding.BaseObservable
import android.databinding.ObservableField

class UserInfo : BaseObservable(){
    var firstName= ObservableField<String>()
    var lastName = ObservableField<String>()
    var mobileNumber : String? = null
    var email :String? = null
    var playerId: String? = null
}