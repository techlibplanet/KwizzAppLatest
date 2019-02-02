package kwizzapp.com.kwizzapp.models

class RazorpayModel {
    class OrderModel(val id : String, val entity : String, val amount : Int, val currency : String,
                     val receipt : String, val status : String, val attempts : Int, val created_at: Long)

    class OrderDetails{
        var amount : String? = null
        var currency : String? = null
        var receipt : String? = null
        var payment_capture : String? = null

    }
}