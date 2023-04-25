package com.checkpoint.rfid_raw_material.utils.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.checkpoint.rfid_raw_material.R
import com.checkpoint.rfid_raw_material.adapter.DevicesListAdapter
import com.checkpoint.rfid_raw_material.utils.dialogs.interfaces.SelectDeviceDialogInterface

class DialogSelectPairDevices(val devices: List<String>,context: Context) : Dialog(context) {
   lateinit var  selectDeviceDialogInterface: SelectDeviceDialogInterface
   private var device =String()
    init {
        setCancelable(false)
    }
    @SuppressLint("SetTextI18n", "ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_select_pair_devices)
        val buttonSelectDevice = findViewById<Button>(R.id.btnSelectDevice)
        val listDevices = findViewById<RecyclerView>(R.id.listViewDevices)
        listDevices.layoutManager= LinearLayoutManager(context)
        val adapter = DevicesListAdapter(devices,DevicesListAdapter.OnClickListener{
            device = it

        })
        listDevices.adapter = adapter


         selectDeviceDialogInterface = context as SelectDeviceDialogInterface

        buttonSelectDevice.setOnClickListener {

            selectDeviceDialogInterface.setDevice(device)
        }
    }


}