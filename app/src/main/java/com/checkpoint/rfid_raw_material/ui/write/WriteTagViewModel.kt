package com.checkpoint.rfid_raw_material.ui.write

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.checkpoint.rfid_raw_material.source.DataRepository
import com.checkpoint.rfid_raw_material.source.RawMaterialsDatabase
import com.checkpoint.rfid_raw_material.source.db.Provider
import com.checkpoint.rfid_raw_material.source.db.Tags
import com.checkpoint.rfid_raw_material.source.db.tblItem
import com.checkpoint.rfid_raw_material.source.model.ProviderModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class WriteTagViewModel (application: Application) : AndroidViewModel(application){
    private var repository: DataRepository

    init {
        repository = DataRepository.getInstance(
            RawMaterialsDatabase.getDatabase(application.baseContext)
        )
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

    suspend fun newTag(version: String,subversion:String,type:String,piece:String,
    idProvider:Int,epc:String): Tags = withContext(Dispatchers.IO) {
        val nowDate: OffsetDateTime = OffsetDateTime.now()
        val formatter: DateTimeFormatter = DateTimeFormatter.ISO_INSTANT

        repository.insertNewTag(
            Tags(
                0,
                version,
                subversion,
                type,
                piece,
                idProvider,
                epc,
                formatter.format(nowDate)
            )
        )
    }
}