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
import com.checkpoint.rfid_raw_material.enums.TypeLoading

class CustomDialogLoader(private val fragment: Fragment, private val typeLoading: TypeLoading): Dialog(fragment.requireContext()) {
    init {
        when(typeLoading){
            TypeLoading.BLUETOOTH_TURNON->{
                setCancelable(true)
            }
            else->{
                setCancelable(false)
            }

        }
    }

    @SuppressLint("SetTextI18n", "ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.custom_dialog_loader)
        val tvMessageLoader =findViewById<TextView>(R.id.tvMessageLoader)
        val lytLoaderView = findViewById<LinearLayout>(R.id.lytLoaderView)
        val lytblueToothAdvice= findViewById<LinearLayout>(R.id.lytblueToothAdvice)

        val resource = context.resources
        lytLoaderView.visibility= View.VISIBLE
        tvMessageLoader.text=when(typeLoading){
            TypeLoading.BLUETOOTH_DEVICE->{
                resource.getString(R.string.loading_text_bluetooth)
            }
            TypeLoading.DEFAULT->{
                resource.getString(R.string.loading_text)
            }
            TypeLoading.BLUETOOTH_DISCOVERY->{
                resource.getString(R.string.bt_message_device_discovery)
            }
            TypeLoading.BLUETOOTH_TURNON->{
                lytLoaderView.visibility = View.GONE
                lytblueToothAdvice.visibility = View.VISIBLE
                resource.getString(R.string.bt_message_device_discovery)
            }


        }

    }
}