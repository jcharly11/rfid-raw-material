package com.checkpoint.rfid_raw_material.utils.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.checkpoint.rfid_raw_material.R

class DialogErrorDeviceConnected(context: Context) : Dialog(context) {
    init {
        setCancelable(true)
    }
    @SuppressLint("SetTextI18n", "ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         setContentView(R.layout.dialog_error_device_connected)

    }


}