package com.checkpoint.rfid_raw_material

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.INVISIBLE
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import com.checkpoint.rfid_raw_material.bluetooth.BluetoothHandler
import com.checkpoint.rfid_raw_material.databinding.ActivityConfirmWriteBinding
import com.checkpoint.rfid_raw_material.handheld.kt.Device
import com.checkpoint.rfid_raw_material.handheld.kt.DeviceInstanceRFID
import com.checkpoint.rfid_raw_material.handheld.kt.interfaces.*
import com.checkpoint.rfid_raw_material.preferences.LocalPreferences
import com.checkpoint.rfid_raw_material.source.DataRepository
import com.checkpoint.rfid_raw_material.source.RawMaterialsDatabase
import com.checkpoint.rfid_raw_material.source.db.Tags
import com.checkpoint.rfid_raw_material.ui.handheld.HandHeldConfigFragment
import com.checkpoint.rfid_raw_material.utils.ReverseStandAlone
import com.checkpoint.rfid_raw_material.utils.dialogs.*
import com.checkpoint.rfid_raw_material.utils.dialogs.interfaces.DialogWriteTagSuccessInterface
import com.zebra.rfid.api3.ReaderDevice
import com.zebra.rfid.api3.Readers
import com.zebra.rfid.api3.TagData
import kotlinx.coroutines.*
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import kotlin.concurrent.thread


class ConfirmWriteActivity :
    ActivityBase(),
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
    private var dialogPrepareReading: DialogPrepareReading? = null
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
    private var repository: DataRepository? = null
    private var tagsDetected = 0
    private var tagData: Array<TagData?>? = null
    private var tagsClose: MutableList<String> = arrayListOf()


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
        lyCreateLog!!.visibility = INVISIBLE
        btnHandHeldGun!!.setOnClickListener {

            deviceInstanceRFID!!.battery()
            val readNumber= localSharedPreferences!!.getReadNumber()

            fragmentHandHeldConfig!!.arguments = bundleOf(
                "readNumber" to readNumber,
                "batteryLevel" to batteryLevel,
                "activity" to "ConfirmWriteActivity")

            loadFragment(fragmentHandHeldConfig!!)

        }
        bluetoothHandler = BluetoothHandler(this)
        contextActivity = this

        var edtNewEPC = binding.edtNewTagEPC
        binding.btnWrite.visibility = INVISIBLE
         tvMessage = binding.tvMessageTriggers


        val arguments = intent.extras
        if (arguments != null) {
            epc = arguments?.getString("epc")

        }

        dialogErrorMultipleTags= DialogErrorMultipleTags(this)
        dialogWriteTag = CustomDialogWriteTag(this)
        dialogLoadingWrite = DialogPrepareTrigger(this)
        dialogPrepareReading = DialogPrepareReading(this)
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

         binding.btnWrite.setOnClickListener {
            write()
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


    override fun onStop() {
        super.onStop()
        device!!.disconnect()
    }

    fun removeFargment(){
        deviceInstanceRFID!!.changeSession(localSharedPreferences!!.getSessionFromPreferences())
        removeFragment(fragmentHandHeldConfig!!)

    }

    override fun handleTagdata(tagData: Array<TagData?>?) {
        for (data in tagData!!){
            tagsClose.add(data!!.tagID)

        }
    }

    fun counterTags(): MutableList<String> {

        Thread.sleep(3000)
        return tagsClose.toSet().toMutableList()


    }

    fun cleanDialogs(){
        uiScope.launch {
            if(dialogErrorMultipleTags!!.isShowing){
                dialogErrorMultipleTags!!.dismiss()
            }
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

    fun read(){
        uiScope.launch {
            dialogPrepareReading!!.show()
        }
        counterTags().apply {
            dialogPrepareReading!!.dismiss()

            this!!.forEach {
                Log.e("DATA VALUE","${it}")
            }

            when(this!!.size){

                0->{
                    uiScope.launch {
                        tvMessage!!.setTextColor(Color.parseColor("#FD8D03"))
                        tvMessage!!.text = "Press the device trigger to Read a tag"
                    }
                }
                1->{

                    tid = tagsClose.get(0)
                    uiScope.launch {
                        tvMessage!!.setTextColor(Color.parseColor("#59B113"))
                        tvMessage!!.text = "Tag $tid  Ready to write"
                        binding.btnWrite.visibility = View.VISIBLE
                        binding.btnWrite.text = "Write tag"
                    }
                }
                else->{
                    uiScope.launch {
                        dialogErrorMultipleTags!!.show()
                        tvMessage!!.setTextColor(Color.parseColor("#FD8D03"))
                        tvMessage!!.text = "Press the device trigger to Read a tag"
                    }

                }
            }
            Log.e("counterTags", "${this.size}")

            tagsClose.clear()
        }
    }

    override fun handleTriggerPress(pressed: Boolean) {
        if(pressed){
            deviceInstanceRFID!!.perform()
            cleanDialogs()

        }else{
            deviceInstanceRFID!!.stop().apply {

               read()
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
        batteryLevel = level
        binding.appRawMaterials.batteryView.setPercent(level)

    }

    override fun isConnected(b: Boolean) {
        if(b){


            deviceInstanceRFID =  DeviceInstanceRFID(device!!.getReaderDevice(),105,"SESSION_0", true)
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

                saveWritedTag(epc!!)
                dialogWriteTagSuccess = DialogWriteTagSuccess(this, epc!!, this)
                uiScope.launch {
                    dialogWriteTagSuccess!!.show()

                }

            }else{
               dialogERRORWriting!!.show()

            }
        }

    }

    fun saveWritedTag(epc: String){
        var rever = ReverseStandAlone()
        rever.hexadecimalToBinaryString(epc)
        CoroutineScope(Dispatchers.IO).launch {
            addTag(
                epc, localSharedPreferences!!.getReadNumber(), rever.getVersion(),
                rever.getSubVersion(), rever.getType(), rever.getPiece(), rever.getSupplier().toInt()
            )
        }
    }

    suspend fun addTag(epc: String, readNumb: Int,version:String, subVersion:String, type:String,
                       piece:String, provider:Int): Tags = withContext(Dispatchers.IO) {
        val nowDate: OffsetDateTime = OffsetDateTime.now()
        val formatter: DateTimeFormatter = DateTimeFormatter.ISO_INSTANT

        repository!!.insertNewTag(
            Tags(
                0,
                readNumb,
                version,
                subVersion,
                type,
                piece,
                provider,
                epc,
                formatter.format(nowDate)
            )
        )
    }
    override fun successRecording() {
        dialogWriteTagSuccess!!.dismiss()
        device!!.disconnect()
        setResult(RESULT_OK)
        finish()
    }

}