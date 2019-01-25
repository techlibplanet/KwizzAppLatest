package kwizzapp.com.kwizzapp.services


import io.reactivex.Observable
import kwizzapp.com.kwizzapp.models.Users
import kwizzapp.com.kwizzapp.viewmodels.CommonResult
import retrofit2.http.*

interface IUser {

    @POST("payu/updateProfileInfo.php")
    fun updateUserInfo(@Body user: Users.UpdateUserInfo) : Observable<CommonResult>

    @POST("payu/insertUserInfo.php")
    fun insertUserInfo(@Body user: Users.InsertUserInfo) : Observable<CommonResult>

    @POST("payu/updateDisplayName.php")
    fun updateDisplayName(@Body updateDisplayName: Users.UpdateDisplayName) : Observable<CommonResult>

    @POST("payu/updateFcmToken.php")
    fun updateFcmToken(@Body updateFcmToken: Users.UpdateFcmToken) : Observable<CommonResult>
}