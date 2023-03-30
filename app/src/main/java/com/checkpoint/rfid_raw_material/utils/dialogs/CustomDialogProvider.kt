package com.checkpoint.rfid_raw_material.utils.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import android.widget.Button
import androidx.fragment.app.Fragment
import com.checkpoint.rfid_raw_material.R
import com.checkpoint.rfid_raw_material.utils.dialogs.interfaces.CustomDialogProviderInterface
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText

class CustomDialogProvider(private val fragment: Fragment): Dialog(fragment.requireContext()) {
    private lateinit var dialogClickButton: CustomDialogProviderInterface
    var tvIdProvider: TextInputEditText?= null
    var tvIdASProvider: TextInputEditText?= null
    var tvNameProvider: TextInputEditText?= null

    init {
        setCancelable(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.custom_dialog_provider)
        //setContentView(R.layout.custom_dialog)
        val btnSaveProvider = findViewById<Button>(R.id.btnSaveProvider)
        val btnCloseDialogProvider = findViewById<FloatingActionButton>(R.id.btnCloseDialogProvider)

        dialogClickButton = fragment as CustomDialogProviderInterface
        tvIdProvider =findViewById(R.id.tvIdProvider)
        tvIdASProvider =findViewById(R.id.tvIdASProvider)
        tvNameProvider =findViewById(R.id.tvNameProvider)

        btnSaveProvider.setOnClickListener{
            dialogClickButton.saveProvider()
        }
        btnCloseDialogProvider.setOnClickListener {
            dialogClickButton.closeDialog()
        }
    }
}