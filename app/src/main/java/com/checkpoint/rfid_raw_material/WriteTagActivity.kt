package com.checkpoint.rfid_raw_material

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.checkpoint.rfid_raw_material.handheld.kt.DeviceConfig
import com.checkpoint.rfid_raw_material.handheld.kt.RFIDHandler
import com.checkpoint.rfid_raw_material.zebra.BatteryHandlerInterface
import com.checkpoint.rfid_raw_material.zebra.ResponseHandlerInterface
 import com.zebra.rfid.api3.*
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
            Log.e("handleTagdata","${it!!.tagEvent}")
            Log.e("handleTagdata","${it!!.memoryBankData}")

            if (it.getOpCode() === ACCESS_OPERATION_CODE.ACCESS_OPERATION_READ &&
                it.getOpStatus() === ACCESS_OPERATION_STATUS.ACCESS_SUCCESS
            ) {
                if (it.getMemoryBankData().length > 0) {
                    Log.d(
                        "TAG",
                        " Mem Bank Data " + it.getMemoryBankData()
                    )
                }
            }
            if (it.isContainsLocationInfo()) {
                val dist: Short = it.LocationInfo.getRelativeDistance()
                Log.d(
                    "TAG",
                    "Tag relative distance $dist"
                )
            }

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