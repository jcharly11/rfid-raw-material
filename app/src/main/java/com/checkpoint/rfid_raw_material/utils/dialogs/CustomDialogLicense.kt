package com.checkpoint.rfid_raw_material.utils.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.checkpoint.rfid_raw_material.R
import com.checkpoint.rfid_raw_material.enums.TypeWarning
 import com.checkpoint.rfid_raw_material.utils.dialogs.interfaces.CustomDialogLicenseInterface


class CustomDialogLicense(val fragment: Fragment, private val typeWarning: TypeWarning
) : Dialog(fragment.requireContext())  {

    private lateinit var customDialogLicenseInterface: CustomDialogLicenseInterface
    init {
        setCancelable(false)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.custom_dialog_license)
        val tvDialogTitle =findViewById<TextView>(R.id.tvTitleDialogLicense)
        val tvDialogWarning =findViewById<TextView>(R.id.tvWarningDialogLicense)
        val buttonAccept = findViewById<Button>(R.id.btnDialogAcceptLicence)

        customDialogLicenseInterface = fragment as CustomDialogLicenseInterface

        when(typeWarning){
            TypeWarning.BLANK_FIELD ->{
                tvDialogTitle.text = context.resources.getString(R.string.blank_field)
                tvDialogWarning.text = context.resources.getString(R.string.validate_license)
            }
            TypeWarning.WRONG_TOKEN -> {
                tvDialogTitle.text = context.resources.getString(R.string.invalidate_token)
                tvDialogWarning.text = context.resources.getString(R.string.token_error)
            }
        }

        buttonAccept.setOnClickListener {
            customDialogLicenseInterface.closeDialog()
        }
    }
}