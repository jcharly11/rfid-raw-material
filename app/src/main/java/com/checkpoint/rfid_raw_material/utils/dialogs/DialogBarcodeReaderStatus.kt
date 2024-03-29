package com.checkpoint.rfid_raw_material.utils.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.Window
import androidx.fragment.app.Fragment
import com.checkpoint.rfid_raw_material.R

class DialogBarcodeReaderStatus(private val fragment: Fragment): Dialog(fragment.requireContext()) {
    init {
        setCancelable(false)
    }
    @SuppressLint("SetTextI18n", "ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_barcode_reader_status)

    }


}