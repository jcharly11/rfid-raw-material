package com.checkpoint.rfid_raw_material.ui.write

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.checkpoint.rfid_raw_material.handheld.BarcodeHandHeldInterface
import com.checkpoint.rfid_raw_material.handheld.kt.DeviceConfig
import com.checkpoint.rfid_raw_material.handheld.kt.HandHeldBarCodeReader
import com.checkpoint.rfid_raw_material.source.DataRepository
import com.checkpoint.rfid_raw_material.source.RawMaterialsDatabase
import com.checkpoint.rfid_raw_material.source.db.Provider
import com.checkpoint.rfid_raw_material.source.db.tblItem
import com.checkpoint.rfid_raw_material.source.model.ProviderModel
import com.checkpoint.rfid_raw_material.zebra.BatteryHandlerInterface
import com.checkpoint.rfid_raw_material.zebra.ResponseHandlerInterface
import com.checkpoint.rfid_raw_material.zebra.ZebraRFIDHandlerImpl
import com.zebra.rfid.api3.ENUM_TRANSPORT
import com.zebra.rfid.api3.ENUM_TRIGGER_MODE
import com.zebra.rfid.api3.SESSION
import com.zebra.rfid.api3.TagData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WriteTagViewModel (application: Application) : AndroidViewModel(application),BarcodeHandHeldInterface {
    private var repository: DataRepository
    private var handHeldBarCodeReader: HandHeldBarCodeReader? = null
    private val _liveCode: MutableLiveData<String> = MutableLiveData()
    @SuppressLint("StaticFieldLeak")
    private var context = application.applicationContext
    var liveCode: LiveData<String> = _liveCode

    init {
        repository = DataRepository.getInstance(
            RawMaterialsDatabase.getDatabase(application.baseContext)
        )
        handHeldBarCodeReader = HandHeldBarCodeReader()
        handHeldBarCodeReader!!.setBarcodeResponseInterface(this)

    }

    suspend fun newProvider(id: Int,idAS:String,nameProvider:String): Provider = withContext(Dispatchers.IO) {
        repository.insertNewProvider(
            Provider(
                0,
                id,
                idAS,
                nameProvider
            )
        )
    }

    suspend fun getProviderList():MutableList<ProviderModel> = withContext(Dispatchers.IO){
        val list= repository.getProviders()
        var listProviders:MutableList<ProviderModel> = mutableListOf()

        list.iterator().forEachRemaining {
            var itemProvider= ProviderModel(id = it.id,it.name)
            listProviders!!.add(itemProvider)
        }
        listProviders= listProviders!!.toMutableList()
        listProviders
    }


    override fun setDataBarCode(code: String){

      _liveCode.value = code

    }

    suspend fun startHandHeldBarCode(){
        handHeldBarCodeReader!!.instance(context, DeviceConfig(
            0,
            SESSION.SESSION_S1,
            "RFD850019323520100189",
            ENUM_TRIGGER_MODE.BARCODE_MODE,
            ENUM_TRANSPORT.BLUETOOTH

        )
        )
    }

    suspend fun disconnectDevice(){
        viewModelScope.launch {
            handHeldBarCodeReader!!.disconnect()
        }
        }

}