package kwizzapp.com.kwizzapp.viewmodels

import android.databinding.BaseObservable

class UserBankDetails : BaseObservable(){
    var firstName : String? = null
    var lastName : String? = null
    var accountNumber : String? =  null
    var ifscCode : String? = null
}