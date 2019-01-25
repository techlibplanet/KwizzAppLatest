package com.technoholicdeveloper.kwizzapp.gateway

import android.app.Activity
import android.app.ProgressDialog
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import com.payumoney.core.PayUmoneyConfig
import com.payumoney.core.PayUmoneyConstants
import com.payumoney.core.PayUmoneySdkInitializer
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager
import kwizzapp.com.kwizzapp.Constants
import kwizzapp.com.kwizzapp.R
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.ProtocolException
import java.net.URL

internal class PayUMoney(val activity: Activity) {

    private lateinit var paymentParam: PayUmoneySdkInitializer.PaymentParam

    /**
     * This function prepares the data for payment and launches payumoney plug n play sdk
     */
    fun launchPayUMoney(amount : Double, firstName : String,mobileNumber : String, email : String, productName : String) {

        val payUMoneyConfig = PayUmoneyConfig.getInstance()

        //Use this to set your custom text on result screen button
        payUMoneyConfig.doneButtonText = "Done"

        //Use this to set your custom title for the activity
        payUMoneyConfig.payUmoneyActivityTitle = "Alchemy Education"

        val builder = PayUmoneySdkInitializer.PaymentParam.Builder()

        val txnId = System.currentTimeMillis().toString() + ""
        val udf1 = ""
        val udf2 = ""
        val udf3 = ""
        val udf4 = ""
        val udf5 = ""

        builder.setAmount(amount)
                .setTxnId(txnId)
                .setPhone(mobileNumber)
                .setProductName(productName)
                .setFirstName(firstName)
                .setEmail(email)
                .setsUrl(Constants.surl)        // Success Url
                .setfUrl(Constants.furl)        // Failed Url
                .setUdf1(udf1)
                .setUdf2(udf2)
                .setUdf3(udf3)
                .setUdf4(udf4)
                .setUdf5(udf5)
                .setIsDebug(false)              // Integration environment - true (Debug)/ false(Production)
                .setKey(Constants.MERCHANT_KEY)
                .setMerchantId(Constants.MERCHANT_ID)

        try {
            paymentParam = builder.build()
            generateHashFromServer(paymentParam)

        } catch (e: Exception) {
            Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
        }

    }

    private fun generateHashFromServer(paymentParam: PayUmoneySdkInitializer.PaymentParam) {


        val params = paymentParam.params

        // lets create the post params
        val postParamsBuffer = StringBuffer()
        postParamsBuffer.append(concatParams(PayUmoneyConstants.KEY, params[PayUmoneyConstants.KEY]!!))
        postParamsBuffer.append(concatParams(PayUmoneyConstants.AMOUNT, params[PayUmoneyConstants.AMOUNT]!!))
        postParamsBuffer.append(concatParams(PayUmoneyConstants.TXNID, params[PayUmoneyConstants.TXNID]!!))
        postParamsBuffer.append(concatParams(PayUmoneyConstants.EMAIL, params[PayUmoneyConstants.EMAIL]!!))
        postParamsBuffer.append(concatParams("productinfo", params[PayUmoneyConstants.PRODUCT_INFO]!!))
        postParamsBuffer.append(concatParams("firstname", params[PayUmoneyConstants.FIRSTNAME]!!))
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF1, params[PayUmoneyConstants.UDF1]!!))
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF2, params[PayUmoneyConstants.UDF2]!!))
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF3, params[PayUmoneyConstants.UDF3]!!))
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF4, params[PayUmoneyConstants.UDF4]!!))
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF5, params[PayUmoneyConstants.UDF5]!!))


        val postParams = if (postParamsBuffer[postParamsBuffer.length - 1] == '&') postParamsBuffer.substring(0, postParamsBuffer.length - 1).toString() else postParamsBuffer.toString()

        // Make an api call
        val getHashesFromServerTask = GetHashesFromServerTask()
        getHashesFromServerTask.execute(postParams)
    }

    /**
     * This AsyncTask generates hash from server.
     */
    private inner class GetHashesFromServerTask : AsyncTask<String, String, String>() {
        private var progressDialog: ProgressDialog? = null

        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog = ProgressDialog(activity)
            progressDialog!!.setMessage("Please wait...")
            progressDialog!!.show()
        }

        override fun doInBackground(vararg postParams: String): String {

            var merchantHash = ""
            try {
                //TODO Below url is just for testing purpose, merchant needs to replace this with their server side hash generation url
                val url = URL(Constants.URL)

                val postParam = postParams[0]

                val postParamsByte = postParam.toByteArray(charset("UTF-8"))

                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                conn.setRequestProperty("Content-Length", postParamsByte.size.toString())
                conn.doOutput = true
                conn.outputStream.write(postParamsByte)

                val responseInputStream = conn.inputStream
                val responseStringBuffer = StringBuffer()
                val byteContainer = ByteArray(1024)
                var i: Int
                i = responseInputStream.read(byteContainer)
                Log.d("TAG", "$i")
                while (i != -1) {
                    Log.d("TAG", "$i")
                    responseStringBuffer.append(String(byteContainer, 0, i))
                    i = responseInputStream.read(byteContainer)
                }

                println("Response " + responseStringBuffer.toString())
                val response = JSONObject(responseStringBuffer.toString())

                val payuHashIterator = response.keys()
                while (payuHashIterator.hasNext()) {
                    val key = payuHashIterator.next()
                    when (key) {
                    /**
                     * This hash is mandatory and needs to be generated from merchant's server side
                     */
                        "payment_hash" -> merchantHash = response.getString(key)
                        else -> {
                        }
                    }
                }

            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: ProtocolException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return merchantHash
        }

        override fun onPostExecute(merchantHash: String) {
            super.onPostExecute(merchantHash)

            progressDialog!!.dismiss()
            if (merchantHash.isEmpty() || merchantHash == "") {
                Toast.makeText(activity, "Could not generate hash", Toast.LENGTH_SHORT).show()
            } else {
                paymentParam.setMerchantHash(merchantHash)
                PayUmoneyFlowManager.startPayUMoneyFlow(paymentParam, activity, R.style.AppTheme, false)
            }
        }
    }




    private fun concatParams(key: String, value: String): String {
        return "$key=$value&"
    }
}