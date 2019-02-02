package kwizzapp.com.kwizzapp.services

import io.reactivex.Observable
import kwizzapp.com.kwizzapp.models.RazorpayModel
import kwizzapp.com.kwizzapp.models.Transactions
import retrofit2.http.Body
import retrofit2.http.POST

interface IRazorpay {

    @POST("payu/GetOrderDetails.php")
    fun getOrderId(@Body orderDetails: RazorpayModel.OrderDetails) : Observable<RazorpayModel.OrderModel>

    @POST("payu/addPointsRazorpay.php")
    fun getTransactionByPaymentId(@Body payment : Transactions.AddPointToServer) : Observable<Transactions.CheckBalance>
}