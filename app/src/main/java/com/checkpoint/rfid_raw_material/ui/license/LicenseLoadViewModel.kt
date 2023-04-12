package com.checkpoint.rfid_raw_material.ui.license

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel


class LicenseLoadViewModel(application: Application) : AndroidViewModel(application) {

    var context = application.baseContext
    fun copyToClpBoard(textToCopy: String){

        val clipboardManager = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", textToCopy)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(context, "Id copied to clipboard", Toast.LENGTH_LONG).show()
    }

}