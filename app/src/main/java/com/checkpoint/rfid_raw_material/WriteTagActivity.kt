package com.checkpoint.rfid_raw_material

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.checkpoint.rfid_raw_material.handheld.kt.Device
import com.checkpoint.rfid_raw_material.handheld.kt.DeviceInstanceBARCODE
import com.checkpoint.rfid_raw_material.handheld.kt.interfaces.BarcodeHandHeldInterface
import com.checkpoint.rfid_raw_material.handheld.kt.interfaces.BatteryHandlerInterface
import com.checkpoint.rfid_raw_material.handheld.kt.interfaces.ResponseHandlerInterface
import com.checkpoint.rfid_raw_material.handheld.kt.DeviceInstanceRFID
import com.checkpoint.rfid_raw_material.handheld.kt.interfaces.DeviceConnectStatusInterface
import com.zebra.rfid.api3.*


class WriteTagActivity : AppCompatActivity(),
    ResponseHandlerInterface ,
    Readers.RFIDReaderEventHandler,
    BatteryHandlerInterface,
    DeviceConnectStatusInterface,
    BarcodeHandHeldInterface{



    private  var  deviceInstanceRFID: DeviceInstanceRFID? = null
    private  var deviceInstanceBARCODE: DeviceInstanceBARCODE? = null
    private lateinit var device: Device
    private var deviceReady = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_tag2)
        device = Device(applicationContext,"RFD850019078523021045",this)

        var btnReadMode = findViewById<Button>(R.id.btnReadMode)
        btnReadMode.setOnClickListener {

            if(deviceInstanceRFID != null){
                deviceInstanceRFID!!.clean()

            }
            deviceInstanceRFID =  DeviceInstanceRFID(device.getReaderDevice())
            deviceInstanceRFID!!.setBatteryHandlerInterface(this)
            deviceInstanceRFID!!.setHandlerInterfacResponse(this)
            deviceInstanceRFID!!.setRfidModeRead()

        }

        var btnBarcode = findViewById<Button>(R.id.btnBarcode)
        btnBarcode.setOnClickListener {

            deviceInstanceBARCODE= DeviceInstanceBARCODE(device.getReaderDevice(),applicationContext)
            deviceInstanceBARCODE!!.setBarCodeHandHeldInterface(this)
        }

    }


    override fun onStart() {
        super.onStart()

        device.connect()

    }

    override fun onDestroy() {
        super.onDestroy()
        device.disconnect()
     }

    override fun handleTagdata(tagData: Array<TagData?>?) {
        Log.e("handleTagdata", "${tagData!!.size}")


    }

    override fun handleTriggerPress(pressed: Boolean) {
        Log.e("handleTriggerPress", "${pressed}")
        if(pressed){
            deviceInstanceRFID!!.perform()
        }else{
            deviceInstanceRFID!!.stop()
        }
    }

    override fun handleStartConnect(connected: Boolean) {
    }

    override fun RFIDReaderAppeared(p0: ReaderDevice?) {
        Log.e("RFIDReaderAppeared", "${p0!!.rfidReader.hostName}")
    }

    override fun RFIDReaderDisappeared(p0: ReaderDevice?) {
        Log.e("RFIDReaderDisappeared", "${p0!!.rfidReader.hostName}")
    }

    override fun batteryLevel(level: Int) {
        Log.e("BatteryLevel", "${level}")
    }

    override fun isConnected(b: Boolean) {
        Log.e("handleStartConnect", "${b}")
        deviceReady = b

    }

    override fun setDataBarCode(code: String) {
        TODO("Not yet implemented")
    }

    override fun connected(status: Boolean) {
        TODO("Not yet implemented")
    }


}