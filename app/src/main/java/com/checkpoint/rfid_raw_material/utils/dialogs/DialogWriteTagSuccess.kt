package com.checkpoint.rfid_raw_material.utils.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import com.checkpoint.rfid_raw_material.R
import com.checkpoint.rfid_raw_material.utils.dialogs.interfaces.DialogWriteTagSuccessInterface

class DialogWriteTagSuccess(context: Context, private var epc: String) : Dialog(context) {

    lateinit var dialogWriteTagSuccessInterface: DialogWriteTagSuccessInterface
     init {
        setCancelable(false)
    }
    @SuppressLint("SetTextI18n", "ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_write_tag_success)

        dialogWriteTagSuccessInterface= context as DialogWriteTagSuccessInterface
        val buttonAccept = findViewById<Button>(R.id.btnAcceptWriteTag)
        val tvEpc = findViewById<TextView>(R.id.tvEpcRecorded)
        tvEpc.text = epc

        buttonAccept.setOnClickListener {
            dialogWriteTagSuccessInterface.successRecording()
        }
    }
}