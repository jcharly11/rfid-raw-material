package com.checkpoint.rfid_raw_material

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.checkpoint.rfid_raw_material.bluetooth.BluetoothHandler
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
    private var bluetoothHandler: BluetoothHandler? = null
    private var deviceName =  String()
    val permissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                  Log.d("ACCESS_FINE_LOCATION","${permissions.getValue(Manifest.permission.ACCESS_FINE_LOCATION)}")
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                  Log.d("ACCESS_COARSE_LOCATION","${permissions.getValue(Manifest.permission.ACCESS_COARSE_LOCATION)}")
            }
            permissions.getOrDefault(Manifest.permission.BLUETOOTH_SCAN, false) -> {
                  Log.d("BLUETOOTH_SCAN","${permissions.getValue(Manifest.permission.BLUETOOTH_SCAN)}")
            }
            permissions.getOrDefault(Manifest.permission.BLUETOOTH_ADMIN, false) -> {
                  Log.d("BLUETOOTH_ADMIN","${permissions.getValue(Manifest.permission.BLUETOOTH_ADMIN)}")
            }
            permissions.getOrDefault(Manifest.permission.BLUETOOTH_CONNECT, false) -> {
                  Log.d("BLUETOOTH_CONNECT","${permissions.getValue(Manifest.permission.BLUETOOTH_CONNECT)}")
            }
            else -> {
                finish()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_tag2)
        bluetoothHandler = BluetoothHandler(this)


        permissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_CONNECT
        ))
        bluetoothHandler = BluetoothHandler(this)
        val devices = bluetoothHandler!!.list()
        if (devices != null) {
            if (devices.size > 0){

                for (device in devices) {

                    if (ActivityCompat.checkSelfPermission(this,Manifest.permission.BLUETOOTH_CONNECT)
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                        Log.e("NO PERMISSION_GRANTED","BLUETOOTH_CONNECT")
                    }


                    if (device.name.contains("RFD8500")) {
                        deviceName += device.name
                    }
                }

            }
        }


        device = Device(applicationContext,"RFD850019078523021045",this)

        var btnReadMode = findViewById<Button>(R.id.btnReadMode)
        btnReadMode.setOnClickListener {

            if(deviceInstanceRFID != null){
                deviceInstanceRFID!!.clean()

            }
            deviceInstanceRFID =  DeviceInstanceRFID(device.getReaderDevice(), 150, "SESSION_1")
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

    }

    override fun connected(status: Boolean) {

    }


}