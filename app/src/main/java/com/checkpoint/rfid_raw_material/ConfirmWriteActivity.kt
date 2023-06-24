package com.checkpoint.rfid_raw_material

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.checkpoint.rfid_raw_material.bluetooth.BluetoothHandler
import com.checkpoint.rfid_raw_material.databinding.ActivityConfirmWriteBinding
import com.checkpoint.rfid_raw_material.handheld.kt.Device
import com.checkpoint.rfid_raw_material.handheld.kt.DeviceInstanceRFID
import com.checkpoint.rfid_raw_material.handheld.kt.interfaces.*
import com.checkpoint.rfid_raw_material.preferences.LocalPreferences
import com.checkpoint.rfid_raw_material.source.DataRepository
import com.checkpoint.rfid_raw_material.source.RawMaterialsDatabase
import com.checkpoint.rfid_raw_material.ui.handheld.HandHeldConfigFragment
import com.checkpoint.rfid_raw_material.utils.dialogs.*
import com.checkpoint.rfid_raw_material.utils.dialogs.interfaces.DialogWriteTagSuccessInterface
import com.zebra.rfid.api3.ReaderDevice
import com.zebra.rfid.api3.Readers
import com.zebra.rfid.api3.TagData
import kotlinx.coroutines.*


class ConfirmWriteActivity : ActivityBase(),
    ResponseHandlerInterface,
    Readers.RFIDReaderEventHandler,
    BatteryHandlerInterface,
    DeviceConnectStatusInterface,
    WritingTagInterface,DialogWriteTagSuccessInterface{
    private var epc: String? = null
    private var deviceName: String? = null
    private var tid: String? = null
    private var deviceInstanceRFID: DeviceInstanceRFID? = null
     private var contextActivity: Context? = null

    private var dialogErrorMultipleTags: DialogErrorMultipleTags? = null
    private var dialogWriteTag: CustomDialogWriteTag? = null
    private var dialogLoadingWrite: DialogPrepareTrigger? = null
    private var dialogERRORWriting: DialogErrorWritingTag? = null
    private var dialogWriteTagSuccess: DialogWriteTagSuccess? = null
    private var bluetoothHandler: BluetoothHandler? = null
    private var tvMessage: TextView? = null
    private lateinit var binding: ActivityConfirmWriteBinding
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private var batteryLevel = 0
    private var fragmentHandHeldConfig : HandHeldConfigFragment? = null
    var repository: DataRepository? = null

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmWriteBinding.inflate(layoutInflater)
        localSharedPreferences = LocalPreferences(application)
        repository = DataRepository.getInstance(
            RawMaterialsDatabase.getDatabase(application.baseContext)
        )
        setContentView(binding.root)

        setSupportActionBar(binding.appRawMaterials.toolbar)

        btnCreateLog = binding.appRawMaterials.imgCreateLog
        batteryView = binding.appRawMaterials.batteryView
        lyCreateLog = binding.appRawMaterials.lyCreateLog
        btnHandHeldGun = binding.appRawMaterials.imgHandHeldGun

        fragmentHandHeldConfig = HandHeldConfigFragment()
        lyCreateLog!!.setOnClickListener{
            GlobalScope.launch {
                withContext(Dispatchers.IO) {
                    logs(
                        "write",
                        repository!!,
                        applicationContext,
                        localSharedPreferences!!.getReadNumber()
                    )

                }
            }
        }
        btnHandHeldGun!!.setOnClickListener {




            deviceInstanceRFID!!.battery()
            val readNumber= localSharedPreferences!!.getReadNumber()

            fragmentHandHeldConfig!!.arguments = bundleOf(
                "readNumber" to readNumber,
                "batteryLevel" to batteryLevel)
            loadFragment(fragmentHandHeldConfig!!)

        }
        bluetoothHandler = BluetoothHandler(this)
        contextActivity = this

        var edtNewEPC = binding.edtNewTagEPC
        binding.btnWrite.setOnClickListener {
             write()
        }
         tvMessage = binding.tvMessageTriggers


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
            device!!.disconnect()
            finish()
        }

    }



    @SuppressLint("MissingPermission")
    override fun onStart() {
        super.onStart()
         bluetoothHandler!!.list()!!.forEach {
            if (it.name.contains("RFD8500")) {
                deviceName += it.name
            }
        }
        device = Device(applicationContext,deviceName!!,this)

        dialogLookingForDevice!!.show()
        device!!.connect()

    }


    override fun onDestroy() {
        super.onDestroy()
        device!!.disconnect()
    }

    fun removeFargment(){
        removeFragment(fragmentHandHeldConfig!!)
    }

    override fun handleTagdata(tagData: Array<TagData?>?) {
        if (tagData!!.isNotEmpty()){
            tid = tagData[0]!!.tagID
            updateMessage(tid!!)
        }
    }

    fun updateMessage(tid: String){
        runOnUiThread {
            tvMessage!!.setTextColor(Color.parseColor("#59B113"))
            tvMessage!!.setText("Tag $tid  Ready to write")
            binding.btnWrite.setText("Finish")
        }
    }
    fun updateMessage2(){
        runOnUiThread {
            tvMessage!!.setTextColor(Color.parseColor("#59B113"))
            tvMessage!!.setText("Twrite process succefull")
        }
    }

    fun write(){

        uiScope.launch {
            withContext(Dispatchers.Main) {
                dialogLoadingWrite!!.show()

                withContext(Dispatchers.IO){
                    deviceInstanceRFID!!.writeTagMode(tid!!,epc!!)

                }
            }
        }

    }

    override fun handleTriggerPress(pressed: Boolean) {
        if(pressed){
            deviceInstanceRFID!!.perform()

        }else{
            deviceInstanceRFID!!.stop()
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
        batteryLevel = level
        binding.appRawMaterials.batteryView.setPercent(level)

    }

    override fun isConnected(b: Boolean) {
        if(b){
            deviceInstanceRFID =  DeviceInstanceRFID(device!!.getReaderDevice(),220,"SESSION_S1", true)
            deviceInstanceRFID!!.setBatteryHandlerInterface(this)
            deviceInstanceRFID!!.setHandlerInterfacResponse(this)
            deviceInstanceRFID!!.setHandlerWriteInterfacResponse(this)
            deviceInstanceRFID!!.setRfidModeRead()
            deviceInstanceRFID!!.battery()

            dialogLookingForDevice!!.dismiss()

        }else{

            dialogErrorDeviceConnected!!.show()
        }
    }


    override fun writingTagStatus(status: Boolean,epcRecorded: String) {
        runOnUiThread {
            dialogLoadingWrite!!.dismiss()

            if (status){

                dialogWriteTagSuccess = DialogWriteTagSuccess(this, epc!!, this)
                dialogWriteTagSuccess!!.show()

            }else{
            dialogERRORWriting!!.show()
            }
        }

    }

    override fun successRecording() {
        device!!.disconnect()
        setResult(RESULT_OK)
        finish()
    }

}