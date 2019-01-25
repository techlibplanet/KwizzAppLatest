package kwizzapp.com.kwizzapp.viewmodels

import android.arch.persistence.room.Ignore
import com.google.gson.annotations.SerializedName

open class CommonResult {
    var isSuccess: Boolean = false
    var message: String = ""
    var data: String = ""
}