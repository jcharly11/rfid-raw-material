package com.checkpoint.rfid_raw_material

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.checkpoint.rfid_raw_material.bluetooth.BluetoothHandler
import com.checkpoint.rfid_raw_material.handheld.kt.Device
import com.checkpoint.rfid_raw_material.handheld.kt.DeviceInstanceRFID
import com.checkpoint.rfid_raw_material.handheld.kt.interfaces.*
import com.checkpoint.rfid_raw_material.utils.dialogs.*
import com.checkpoint.rfid_raw_material.utils.dialogs.interfaces.DialogWriteTagSuccessInterface
import com.google.android.material.textfield.TextInputEditText
import com.zebra.rfid.api3.ReaderDevice
import com.zebra.rfid.api3.Readers
import com.zebra.rfid.api3.TagData


class ConfirmWriteActivity : AppCompatActivity(),
    ResponseHandlerInterface,
    Readers.RFIDReaderEventHandler,
    BatteryHandlerInterface,
    DeviceConnectStatusInterface,
    WritingTagInterface,
    DialogWriteTagSuccessInterface {
    private var epc: String? = null
    private var deviceName: String? = null
    private var tid: String? = null
    private var deviceInstanceRFID: DeviceInstanceRFID? = null
    private var device: Device? = null
    private var readyToWrite = false

    private var dialogErrorMultipleTags: DialogErrorMultipleTags? = null
    private var dialogWriteTag: CustomDialogWriteTag? = null
    private var dialogLoadingWrite: DialogPrepareTrigger? = null
    private var dialogERRORWriting: DialogErrorWritingTag? = null
    private var dialogWriteTagSuccess: DialogWriteTagSuccess? = null
    private var dialogLookingForDevice: DialogLookingForDevice? = null
    private var dialogErrorDeviceConnected: DialogErrorDeviceConnected? = null
    private var bluetoothHandler: BluetoothHandler? = null
    private var tvMessage: TextView? = null

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_write)
        var edtNewEPC: TextInputEditText= findViewById(R.id.edtNewTagEPC)
        tvMessage = findViewById(R.id.tvMessageTriggers)


        val arguments = intent.extras
        if (arguments != null) {
            epc = arguments?.getString("epc")

        }

        dialogErrorMultipleTags= DialogErrorMultipleTags(this)
        dialogWriteTag = CustomDialogWriteTag(this)
        dialogLoadingWrite = DialogPrepareTrigger(this)
        dialogERRORWriting = DialogErrorWritingTag(this)
        dialogErrorDeviceConnected = DialogErrorDeviceConnected(this)
        dialogLookingForDevice= DialogLookingForDevice(this)

        edtNewEPC.setText(epc)
        edtNewEPC.isEnabled = false

        var btnCancel:Button= findViewById(R.id.btnCancelWrite)
        btnCancel.setOnClickListener {
          finish()
        }
        bluetoothHandler!!.list()!!.forEach {
            if (it.name.contains("RFD8500")) {
                deviceName += it.name
            }
        }
        device = Device(applicationContext,deviceName!!,this)

    }



    override fun onStart() {
        super.onStart()
        dialogLookingForDevice!!.show()
        device!!.connect()

    }

    override fun onDestroy() {
        super.onDestroy()
        device!!.disconnect()
    }

    override fun successRecording() {
        setResult(RESULT_OK)
        finish()
    }

    override fun handleTagdata(tagData: Array<TagData?>?) {
        if (tagData!!.isNotEmpty()){
            tid = tagData[0]!!.tagID
            readyToWrite = true
            this.runOnUiThread {
                tvMessage!!.text = "Press the trigger again to perform write "
            }
        }
    }


    override fun handleTriggerPress(pressed: Boolean) {
        if (readyToWrite && pressed){
                dialogWriteTag!!.show()
                deviceInstanceRFID!!.writeTagMode(tid!!,epc!!)
        }
        else{
            if(pressed){
                deviceInstanceRFID!!.perform()
            }else{
                deviceInstanceRFID!!.stop()
            }
        }
    }

    override fun handleStartConnect(connected: Boolean) {
        Log.e("handleStartConnect", "${connected}")
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
        if(b){
            deviceInstanceRFID =  DeviceInstanceRFID(device!!.getReaderDevice(),220,"SESSION_S1", true)
            deviceInstanceRFID!!.setBatteryHandlerInterface(this)
            deviceInstanceRFID!!.setHandlerInterfacResponse(this)
            deviceInstanceRFID!!.setHandlerWriteInterfacResponse(this)
            deviceInstanceRFID!!.setRfidModeRead()
            dialogLookingForDevice!!.dismiss()

        }else{

            dialogErrorDeviceConnected!!.show()
        }
    }


    override fun writingTagStatus(status: Boolean,epcRecorded: String) {
        readyToWrite = false
        dialogWriteTag!!.dismiss()

        if(status){
            dialogWriteTagSuccess = DialogWriteTagSuccess(this,epcRecorded)
            dialogWriteTagSuccess!!.show()
        }else{

            dialogERRORWriting!!.show()
        }
    }
}