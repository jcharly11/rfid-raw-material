package com.checkpoint.rfid_raw_material

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.checkpoint.rfid_raw_material.handheld.kt.DeviceConfig
import com.checkpoint.rfid_raw_material.handheld.kt.RFIDHandler
import com.checkpoint.rfid_raw_material.zebra.BatteryHandlerInterface
import com.checkpoint.rfid_raw_material.zebra.ResponseHandlerInterface
import com.checkpoint.rfid_raw_material.zebra.ZebraRFIDHandlerImpl
import com.zebra.barcode.sdk.BarcodeDataEventArgs
import com.zebra.barcode.sdk.BarcodeDataListener
import com.zebra.rfid.api3.ENUM_TRANSPORT
import com.zebra.rfid.api3.ENUM_TRIGGER_MODE
import com.zebra.rfid.api3.SESSION
import com.zebra.rfid.api3.TagData
import kotlinx.coroutines.launch

class WriteTagActivity : AppCompatActivity(),ResponseHandlerInterface,BatteryHandlerInterface {

   private lateinit var rfidHandler: RFIDHandler
   private var writeMode: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_tag)
    }

    override fun onStart() {
        super.onStart()


        rfidHandler = RFIDHandler(
            this,
            DeviceConfig(
                150,
                SESSION.SESSION_S1,
                "RFD850019323520100189",
                ENUM_TRIGGER_MODE.RFID_MODE,
                ENUM_TRANSPORT.BLUETOOTH

            )
        )

        rfidHandler.setResponseHandlerInterface(this)
        rfidHandler.setBatteryHandlerInterface(this)

    }

    override fun handleTagdata(tagData: Array<TagData?>?) {
        tagData!!.iterator().forEachRemaining {
            Log.e("handleTagdata","${it!!.tagID}")
        }

    }

    override fun handleTriggerPress(pressed: Boolean) {

         lifecycleScope.launch {

            if (pressed){
                if (writeMode){
                    rfidHandler.write("","","")
                }else{
                    rfidHandler!!.perform()
                }

            }else{
                rfidHandler!!.stop()
            }

        }

    }

    override fun handleStartConnect(connected: Boolean) {
        Log.e("handleStartConnect","${connected}")
    }

    override fun batteryLevel(level: Int) {
        Log.e("batteryLevel","${level}")
    }


}