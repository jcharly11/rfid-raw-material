package com.checkpoint.rfid_raw_material.utils.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.Window
import android.widget.Button
import androidx.fragment.app.Fragment
import com.checkpoint.rfid_raw_material.R
import com.checkpoint.rfid_raw_material.utils.interfaces.CustomDialogWriteTagInterface
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CustomDialogWriteTag(val fragment: Fragment) : Dialog(fragment.requireContext()) {
    private lateinit var customDialogWriteTagInterface: CustomDialogWriteTagInterface

    init {
        setCancelable(false)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.custom_dialog_write_tag)

        val buttonNext = findViewById<Button>(R.id.btnDialogNextWrite)
        val buttonCancel = findViewById<Button>(R.id.btnDialogCancelWrite)
        val buttonCloseDialog = findViewById<FloatingActionButton>(R.id.btnCloseDialogWrite)

        customDialogWriteTagInterface = fragment as CustomDialogWriteTagInterface
        buttonNext.setOnClickListener {
            customDialogWriteTagInterface.finishWrite()
        }
        buttonCancel.setOnClickListener {
            customDialogWriteTagInterface.closeDialogWrite()
        }

        buttonCloseDialog.setOnClickListener {
            customDialogWriteTagInterface.closeDialogWrite()
        }
    }
}