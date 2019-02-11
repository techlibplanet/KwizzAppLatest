package kwizzapp.com.kwizzapp.models

import kwizzapp.com.kwizzapp.viewmodels.CommonResult

class Transactions {

    class CheckBalance : CommonResult() {
        var mobileNumber: String? = null
        val balance: Double = 0.0
    }

    class SubtractBalance {
        var firstName: String? = null
        var lastName: String? = null
        var playerId: String? = null
        var mobileNumber: String? = null
        var email: String? = null
        var productInfo: String? = null
        var amount: Double? = null
        var addedOn: String? = null
        var createdOn: String? = null
        var transactionType: String? = null
        var status: String? = null
    }

    class AddPointToServer {
        var firstName: String? = null
        var lastName: String? = null
        var playerId: String? = null
        var mobileNumber: String? = null
        var email: String? = null
        var productInfo: String? = null
        var amount: Double? = null
        var txnId: String? = null
        var paymentId: String? = null
        var addedOn: String? = null
        var createdOn: String? = null
        var transactionType: String? = null
        var status: String? = null
        var bankRefNumber: String? = null
        var bankCode: String? = null

    }

    class WithdrawalPointsToServer {
        var firstName: String? = null
        var lastName: String? = null
        var playerId: String? = null
        var mobileNumber: String? = null
        var email: String? = null
        var productInfo: String? = null
        var amount: Double? = null
        var txnId: String? = null
        var addedOn: String? = null
        var createdOn: String? = null
        var transactionType: String? = null
        var status: String? = null
        var accountNumber: String? = null
        var ifscCode: String? = null
    }

    class TransferPointsToServer {
        var firstName: String? = null
        var lastName: String? = null
        var playerId: String? = null
        var mobileNumber: String? = null
        var transferToNumber: String? = null
        var email: String? = null
        var amount: Double? = null
        var txnId: String? = null
        var addedOn: String? = null
        var createdOn: String? = null
        var status: String? = null
    }

    class ResultBalance {
        var playerId: String? = null
        var displayName: String? = null
        var amount: Double? = 0.0
        var timeStamp: String? = null
        var productInfo: String? = null
    }

    class UpdateLoosePoints {
        var playerId: String? = null
        var amount: Double? = null
        var timeStamp: String? = null
    }

    class FetchTransaction : CommonResult() {
        var transactions = mutableListOf<TransactionDetails>()
    }

    class TransactionDetails {
        val firstName: String? = null
        val lastName: String? = null
        val mobileNumber: String? = null
        val transferTo: String? = null
        val receivedFrom: String? = null
        val amount: Double = 0.0
        val txnId: String? = null
        val createdOn: String? = null
        val transactionType: String? = null
        val status: String? = null
        val productInfo: String? = null
    }
}