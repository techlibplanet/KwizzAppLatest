package com.example.mayank.kwizzapp.dialog

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import kwizzapp.com.kwizzapp.R
import org.jetbrains.anko.find

class ShowDialog : View.OnClickListener {

    private lateinit var activity: Activity
    private lateinit var dialog: Dialog
    private lateinit var bigTitle: TextView
    private lateinit var smallTitle: TextView
    private lateinit var statusImage: ImageView
    private lateinit var layoutResult : LinearLayout
    private lateinit var messageText: TextView

    fun dialog(activity: Activity, title: String, message: String) {
        this.activity = activity
        dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_layout)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val titleTextView = dialog.findViewById<TextView>(R.id.textViewTitle)
        val messageTextView = dialog.findViewById<TextView>(R.id.textViewMessage)

        titleTextView.text = title
        messageTextView.text = message

        val okay = dialog.findViewById<Button>(R.id.buttonOk)

        okay.setOnClickListener(this)

        dialog.show()
        dialog.window.setLayout(800, 600)
    }

    fun showResultDialog(activity: Activity, titleBig: String, titleSmall: String, message : String, imageResource : Int) {
        this.activity = activity
        dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.result_dialog_layout)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        statusImage = dialog.find(R.id.statusImage)
        bigTitle = dialog.find(R.id.titleText)
        smallTitle = dialog.find(R.id.messageText)
        messageText = dialog.find(R.id.description)
        layoutResult = dialog.find(R.id.layout_result)

        statusImage.setImageResource(imageResource)
        bigTitle.text = titleBig
        smallTitle.text = titleSmall
        messageText.text = message

        layoutResult.setOnClickListener(this)

        dialog.show()
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.buttonOk -> dialog.dismiss()
            R.id.layout_result -> dialog.dismiss()
        }
    }
}