package com.checkpoint.rfid_raw_material.utils

import android.view.View
import android.widget.Toast
import com.checkpoint.rfid_raw_material.source.DataRepository
import com.checkpoint.rfid_raw_material.source.db.Provider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProviderInternals(var repository: DataRepository) {

    suspend fun saveProvider(idProvider: String,idASProvider: String,nameProvider: String):Boolean = withContext(
        Dispatchers.IO) {

        if (idProvider.isNotEmpty() && idASProvider.isNotEmpty() && nameProvider.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                newProvider(idProvider.toInt(), idASProvider, nameProvider).let {
                    true
                }
            }

        }
        false

    }

    suspend fun newProvider(id: Int,idAS:String,nameProvider:String): Provider = withContext(
        Dispatchers.IO) {
        repository.insertNewProvider(
            Provider(
                0,
                id,
                idAS,
                nameProvider
            )
        )
    }

    suspend fun deleteProvider(idProvider: Int) = withContext(Dispatchers.IO){
        repository.deleteProvider(idProvider)
    }
}