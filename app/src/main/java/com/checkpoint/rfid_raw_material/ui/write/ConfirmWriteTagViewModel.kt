package com.checkpoint.rfid_raw_material.ui.write

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.checkpoint.rfid_raw_material.bluetooth.BluetoothHandler
import com.checkpoint.rfid_raw_material.handheld.ResponseHandlerInterface
import com.checkpoint.rfid_raw_material.handheld.WritingTagInterface
import com.checkpoint.rfid_raw_material.handheld.kt.DeviceConfig
import com.checkpoint.rfid_raw_material.handheld.kt.RFIDHandler
import com.zebra.rfid.api3.ENUM_TRANSPORT
import com.zebra.rfid.api3.ENUM_TRIGGER_MODE
import com.zebra.rfid.api3.SESSION
import com.zebra.rfid.api3.TagData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConfirmWriteTagViewModel(application: Application) : AndroidViewModel(application),
    ResponseHandlerInterface,WritingTagInterface{

    private val _liveTID: MutableLiveData<String> = MutableLiveData("")
    var liveTID: LiveData<String> = _liveTID

    private val _readyToRead: MutableLiveData<Boolean> = MutableLiveData(false)
    var readyToRead: LiveData<Boolean> = _readyToRead

    private val _writeComplete: MutableLiveData<Boolean> = MutableLiveData(false)
    var writeComplete: LiveData<Boolean> = _writeComplete

    private var writeMode: Boolean = false
    private var epc:String ?= null
    private var tid:String ?= null
    private var pass:String ?= null
    private var deviceName: String ?= null

    @SuppressLint("StaticFieldLeak")
    private var context = application.applicationContext
    private var rfidHandler: RFIDHandler?= null
    private var bluetoothHandler: BluetoothHandler? = null

    @SuppressLint("MissingPermission")
    fun initReaderRFID() {
        bluetoothHandler = BluetoothHandler(context)
        val devices = bluetoothHandler!!.list()

        if (devices != null) {
            for (device in devices) {
                if (device.name.contains("RFD8")) {
                    deviceName = device.name
                }
            }
        }

        rfidHandler = RFIDHandler(
            context,
            DeviceConfig(
                150,
                SESSION.SESSION_S1,
                deviceName!!,
                ENUM_TRIGGER_MODE.RFID_MODE,
                ENUM_TRANSPORT.BLUETOOTH
            )
        )
        rfidHandler!!.setResponseHandlerInterface(this)
        rfidHandler!!.setWriteTagHandlerInterface(this)
    }



    override fun handleTagdata(tagData: Array<TagData?>?) {

        _liveTID.postValue(tagData?.get(0)!!.tagID)

     }

    override fun handleTriggerPress(pressed: Boolean) {
        viewModelScope.launch {
            if (pressed){
                if (writeMode){
                        rfidHandler!!.write(tid!!,epc!!,"0").let {
                        writeMode=false

                    }

                }else{
                    rfidHandler!!.perform()
                }

            }else{
                rfidHandler!!.stop()
            }
        }
    }

    override fun handleStartConnect(connected: Boolean) {
        Log.e("handleStartConnect","$connected")
        if (!connected){

            rfidHandler!!.Connect()
        }
        _readyToRead.postValue(connected)

    }

    suspend fun disconectDevice(){
        viewModelScope.launch {

        rfidHandler!!.disconnect()

            }
    }

    suspend fun prepareToWrite(_tid: String,_epc: String,_pass: String):Boolean = withContext(Dispatchers.IO){
        tid = _tid
        epc = _epc
        pass = _pass

        Log.e("prepareToWrite","$tid,$epc,$pass")

        try {

            writeMode =  rfidHandler!!.prepareReaderToWrite()
            writeMode

        }catch (ex: Exception){

            false
        }

    }


    override fun writingTagStatus(status: Boolean) {
        _writeComplete.postValue(status)
    }
}