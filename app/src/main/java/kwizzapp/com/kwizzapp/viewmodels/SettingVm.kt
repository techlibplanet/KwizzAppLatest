package kwizzapp.com.kwizzapp.viewmodels

class SettingVm {
    class SettingMenuVm(val imageSource : Int, val title : String)

    class ProfileVm(var textLabel : String, val textData : String)

    class BankDetailVm(var textLabel : String, val textData : String)

    class PoliciesVm(val policiesIcon : Int, val textLabel : String)
}