package com.checkpoint.rfid_raw_material.utils.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.checkpoint.rfid_raw_material.R

class DialogWriteTagConfirmation(private val fragment: Fragment,private val data: Pair<String,String>): Dialog(fragment.requireContext()) {
    init {
        setCancelable(false)
    }
    @SuppressLint("SetTextI18n", "ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_write_tag_confirmation)
        val tvTagId =findViewById<TextView>(R.id.tvTagId)
        val tvTagEPC = findViewById<TextView>(R.id.tvTagEPC)


    }


}