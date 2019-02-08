package com.example.mayank.kwizzapp.transactions

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import kwizzapp.com.kwizzapp.R
import kwizzapp.com.kwizzapp.helper.Converters
import kwizzapp.com.kwizzapp.helper.Global
import org.jetbrains.anko.find


//class TransactionDetailViewHolder(val dataBinding: TransactionRowBinding) : RecyclerView.ViewHolder(dataBinding.root) {
//
//    fun bindView(context: Context, transactionVm: TransactionDetailsVm) {
//        dataBinding.transactionDetailVm = transactionVm
//        if (transactionVm.transactionType.toString() == "Debited") {
//            dataBinding.transactionDetailVm?.textAmount = "- ${transactionVm.textAmount}"
//            dataBinding.textTransactionAmount.setTextColor(Color.RED)
//        } else {
//            dataBinding.transactionDetailVm?.textAmount = "+ ${transactionVm.textAmount}"
//            dataBinding.textTransactionAmount.setTextColor(context.resources.getColor(R.color.colorGreen))
//        }
//    }
//}
class TransactionDetailViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

    fun bindView(context : Context, transactionDetailVm: TransactionDetailsVm){
        val textUserName = itemView.find<TextView>(R.id.text_user_name)
        val textAmount = itemView.find<TextView>(R.id.text_transaction_amount)
        val textTimeStamp = itemView.find<TextView>(R.id.text_time_stamp)
        val textDescription = itemView.find<TextView>(R.id.text_description)
        val imageUser = itemView.find<ImageView>(R.id.user_image)

        textUserName.text = transactionDetailVm.textUserName
        textTimeStamp.text = Global.getDateFormat(Converters.fromTimestamp(transactionDetailVm.textTimeStamp?.toLong())!!)
        textDescription.text = transactionDetailVm.textDescription
        if (transactionDetailVm.transactionType.toString() == "Debited"){
            textAmount.text = "- ${transactionDetailVm.textAmount}"
            textAmount.setTextColor(Color.RED)
        }else{
            textAmount.text = "+ ${transactionDetailVm.textAmount}"
            textAmount.setTextColor(context.resources.getColor(R.color.colorGreen))
        }
    }
}