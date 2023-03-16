package com.checkpoint.rfid_raw_material.ui.write

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.checkpoint.rfid_raw_material.handheld.kt.DeviceConfig
import com.checkpoint.rfid_raw_material.handheld.kt.RFIDHandler
import com.checkpoint.rfid_raw_material.zebra.ResponseHandlerInterface
import com.zebra.rfid.api3.ENUM_TRANSPORT
import com.zebra.rfid.api3.ENUM_TRIGGER_MODE
import com.zebra.rfid.api3.SESSION
import com.zebra.rfid.api3.TagData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConfirmWriteTagViewModel(application: Application) : AndroidViewModel(application),
    ResponseHandlerInterface{

    private val _liveTID: MutableLiveData<String> = MutableLiveData("")
    var liveTID: LiveData<String> = _liveTID

    private val _readyToRead: MutableLiveData<Boolean> = MutableLiveData(false)
    var readyToRead: LiveData<Boolean> = _readyToRead


    private var writeMode: Boolean = false
    private var epc:String ?= null
    private var tid:String ?= null
    private var pass:String ?= null

    @SuppressLint("StaticFieldLeak")
    private var context = application.applicationContext



    private var rfidHandler: RFIDHandler?= null

    suspend fun initReaderRFID() {

        rfidHandler = RFIDHandler(
            context,
            DeviceConfig(
                150,
                SESSION.SESSION_S1,
                "RFD850019323520100189",
                ENUM_TRIGGER_MODE.RFID_MODE,
                ENUM_TRANSPORT.BLUETOOTH
            )
        )
        rfidHandler!!.setResponseHandlerInterface(this)
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
        Log.e("handleStartConnect","${connected}")
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

        writeMode =  rfidHandler!!.prepareReaderToWrite()
        writeMode
    }

    suspend fun prepareToRead(){
        viewModelScope.launch {

            rfidHandler!!.prepareReaderToRead()
            writeMode = false
        }
    }
}