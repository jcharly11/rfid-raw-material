package com.checkpoint.rfid_raw_material.utils.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.Window
import android.widget.Button
import androidx.fragment.app.Fragment
import com.checkpoint.rfid_raw_material.R
import com.checkpoint.rfid_raw_material.enums.TypeInventory
import com.checkpoint.rfid_raw_material.utils.dialogs.interfaces.CustomDialogInvalidTagInterface
import com.checkpoint.rfid_raw_material.utils.dialogs.interfaces.CustomDialogInventoryInterface
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CustomDIalogInvalidTag(private val fragment: Fragment) : Dialog(fragment.requireContext()) {
    private lateinit var customDialogInvalidTagInterface: CustomDialogInvalidTagInterface

    init {
        setCancelable(false)
    }

    @SuppressLint("SetTextI18n", "ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.custom_dialog_invalid_tag)

        val buttonNext = findViewById<Button>(R.id.btnDialogContinue)
        val buttonCancel = findViewById<Button>(R.id.btnDialogCancelInvalid)
        val buttonCloseDialog = findViewById<FloatingActionButton>(R.id.btnCloseDialogInvalid)
        customDialogInvalidTagInterface = fragment as CustomDialogInvalidTagInterface




        buttonNext.setOnClickListener {
            customDialogInvalidTagInterface.writeTag()
        }
        buttonCancel.setOnClickListener {
            customDialogInvalidTagInterface.closeDialog()
        }

        buttonCloseDialog.setOnClickListener {
            customDialogInvalidTagInterface.closeDialog()
        }

    }
}