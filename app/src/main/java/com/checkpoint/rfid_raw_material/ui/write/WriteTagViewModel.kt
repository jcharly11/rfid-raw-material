package com.checkpoint.rfid_raw_material.ui.write

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.checkpoint.rfid_raw_material.source.DataRepository
import com.checkpoint.rfid_raw_material.source.RawMaterialsDatabase
import com.checkpoint.rfid_raw_material.source.db.Provider
import com.checkpoint.rfid_raw_material.source.db.tblItem
import com.checkpoint.rfid_raw_material.source.model.ProviderModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
}