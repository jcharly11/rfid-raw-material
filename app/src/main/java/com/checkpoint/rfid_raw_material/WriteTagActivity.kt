package com.checkpoint.rfid_raw_material

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.checkpoint.rfid_raw_material.zebra.BatteryHandlerInterface
import com.checkpoint.rfid_raw_material.zebra.ResponseHandlerInterface
import com.checkpoint.rfid_raw_material.zebra.ZebraRFIDHandlerImpl
import com.zebra.rfid.api3.TagData

private  var zebraRFIDHandlerImpl: ZebraRFIDHandlerImpl? = null
private var maxLevel = 300

class WriteTagActivity : AppCompatActivity(), ResponseHandlerInterface, BatteryHandlerInterface {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_tag)
    }

    override fun onStart() {
        super.onStart()
        zebraRFIDHandlerImpl = ZebraRFIDHandlerImpl()
        zebraRFIDHandlerImpl?.listener(this, this)
        zebraRFIDHandlerImpl?.start(application, 150,"RFD850019323520100189", "SESSION_1")

    }

    override fun handleTagdata(tagData: Array<TagData?>?) {
        val code = tagData?.get(0)?.tagID.toString()
        val rssi = tagData?.get(0)?.peakRSSI.toString()
        Log.e("RSSI",rssi)
        Log.e("CODE",code)

    }

    override fun handleTriggerPress(pressed: Boolean) {
        if (pressed) {
            zebraRFIDHandlerImpl?.performWriteTag()
        } else {
            zebraRFIDHandlerImpl?.stop()
        }
    }

    override fun handleStartConnect(connected: Boolean) {
       Log.e("handleStartConnect", "handleStartConnect : $connected")
    }

    override fun batteryLevel(level: Int) {
        Log.e("batteryLevel", "batteryLevel : $level")
    }
}