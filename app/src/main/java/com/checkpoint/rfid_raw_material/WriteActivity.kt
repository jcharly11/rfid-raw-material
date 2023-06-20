package com.checkpoint.rfid_raw_material

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import com.checkpoint.rfid_raw_material.bluetooth.BluetoothHandler
import com.checkpoint.rfid_raw_material.handheld.kt.Device
import com.checkpoint.rfid_raw_material.handheld.kt.DeviceInstanceBARCODE
import com.checkpoint.rfid_raw_material.handheld.kt.DeviceInstanceRFID
import com.checkpoint.rfid_raw_material.handheld.kt.interfaces.*
import com.checkpoint.rfid_raw_material.utils.dialogs.DialogErrorDeviceConnected
import com.checkpoint.rfid_raw_material.utils.dialogs.DialogErrorMultipleTags
import com.checkpoint.rfid_raw_material.utils.dialogs.DialogLookingForDevice
import com.checkpoint.rfid_raw_material.utils.dialogs.DialogSelectPairDevices
import com.zebra.rfid.api3.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter


class WriteTagActivity : AppCompatActivity(),
    ResponseHandlerInterface ,
    Readers.RFIDReaderEventHandler,
    BatteryHandlerInterface,
    DeviceConnectStatusInterface,
    BarcodeHandHeldInterface,
    WritingTagInterface{



    private  var  deviceInstanceRFID: DeviceInstanceRFID? = null
    private  var deviceInstanceBARCODE: DeviceInstanceBARCODE? = null
    private var dialogLookingForDevice: DialogLookingForDevice? = null
    private var dialogErrorDeviceConnected: DialogErrorDeviceConnected? = null
    private var dialogErrorMultipleTags: DialogErrorMultipleTags? = null
    private lateinit var device: Device
    private var deviceReady = false
    private var bluetoothHandler: BluetoothHandler? = null
    private var deviceName =  String()
    private var editextTagid: EditText? = null
    private var editextEPC: EditText? = null

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
            permissions.getOrDefault(Manifest.permission.WRITE_EXTERNAL_STORAGE, false) -> {
            }
            permissions.getOrDefault(Manifest.permission.READ_EXTERNAL_STORAGE, false) -> {
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
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,        ))

        dialogLookingForDevice  = DialogLookingForDevice(this)
        dialogErrorDeviceConnected =  DialogErrorDeviceConnected(this)
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


        }

        editextTagid= findViewById<EditText>(R.id.edtTagID)
        editextEPC = findViewById<EditText>(R.id.edtEPC)

        var btnWrite = findViewById<Button>(R.id.btnWrite)
        btnWrite.setOnClickListener {
            var tid = editextTagid!!.text.toString()
            var epc = editextEPC!!.text.toString()
            deviceInstanceRFID!!.writeTagMode(tid,epc)
        }
        var btnBarcode = findViewById<Button>(R.id.btnBarcode)
        btnBarcode.setOnClickListener {

            deviceInstanceBARCODE= DeviceInstanceBARCODE(device.getReaderDevice(),applicationContext)
            deviceInstanceBARCODE!!.setBarCodeHandHeldInterface(this)
        }

        var btnTestLog= findViewById<Button>(R.id.btnTestLog)
        btnTestLog.setOnClickListener {


            val dir = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)}/raw_materials/"
            val directory3= File(dir)

            if(!directory3.exists()){
                directory3.mkdir()
            }

            val file3 = File(dir, "example2.txt")

            FileWriter(file3).use { fileWriter -> fileWriter.append("Writing to file!")}


            var a =0

        }

    }


    override fun onStart() {
        super.onStart()
        dialogLookingForDevice!!.show()
        device.connect()
        deviceInstanceRFID =  DeviceInstanceRFID(device.getReaderDevice(),220,"SESSION_S1", true)
        deviceInstanceRFID!!.setBatteryHandlerInterface(this)
        deviceInstanceRFID!!.setHandlerInterfacResponse(this)
        deviceInstanceRFID!!.setHandlerWriteInterfacResponse(this)
        deviceInstanceRFID!!.setRfidModeRead()

    }

    override fun onDestroy() {
        super.onDestroy()
        device.disconnect()
    }

    override fun handleTagdata(tagData: Array<TagData?>?) {
        tagData!!.iterator().forEach {
            Log.e("tagID", "${it!!.tagID}")
            editextTagid!!.setText(it!!.tagID)

        }


        //deviceInstanceRFID!!.erase(tagData?.get(0)?.tagID.toString(),"90801A249B1F10A06C96AFF20001E240")
        // deviceInstanceRFID!!.readData(tagData?.get(0)?.tagID.toString())

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

        if(deviceReady){
            dialogLookingForDevice!!.dismiss()
        }else{
            dialogErrorDeviceConnected!!.show()
        }

    }

    override fun setDataBarCode(code: String) {
        Log.e("BARCODE:","$code")
    }

    override fun connected(status: Boolean) {
        TODO("Not yet implemented")
    }

    override fun writingTagStatus(status: Boolean) {
        Log.e("writingTagStatus", "${status}")

    }


}