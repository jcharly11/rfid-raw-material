package com.checkpoint.rfid_raw_material

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.checkpoint.rfid_raw_material.handheld.BarcodeHandHeldInterface
import com.checkpoint.rfid_raw_material.handheld.kt.DeviceConfig
import com.checkpoint.rfid_raw_material.handheld.kt.HandHeldBarCodeReader
import com.zebra.rfid.api3.ENUM_TRANSPORT
import com.zebra.rfid.api3.ENUM_TRIGGER_MODE
import com.zebra.rfid.api3.SESSION
import com.zebra.scannercontrol.*
import com.zebra.scannercontrol.DCSSDKDefs.DCSSDK_COMMAND_OPCODE
import com.zebra.scannercontrol.DCSSDKDefs.DCSSDK_RESULT
import kotlinx.coroutines.launch

class BarCodeActivity : AppCompatActivity(),BarcodeHandHeldInterface {

    private var handHeldBarCodeReader: HandHeldBarCodeReader? = null
    private var codeBar: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bar_code)
        lifecycleScope.launch {
            handHeldBarCodeReader = HandHeldBarCodeReader()
            handHeldBarCodeReader!!.instance(applicationContext, DeviceConfig(
                240,
                SESSION.SESSION_S1,
                "RFD850019323520100189",
                ENUM_TRIGGER_MODE.BARCODE_MODE,
                ENUM_TRANSPORT.BLUETOOTH

            ))


        }
        handHeldBarCodeReader!!.setBarcodeResponseInterface(this)
    }

    override fun setDataBarCode(code: String) {
        Log.e("setData",code)
    }

}