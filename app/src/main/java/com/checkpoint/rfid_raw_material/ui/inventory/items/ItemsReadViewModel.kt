package com.checkpoint.rfid_raw_material.ui.inventory.items

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.checkpoint.rfid_raw_material.preferences.LocalPreferences
import com.checkpoint.rfid_raw_material.source.DataRepository
import com.checkpoint.rfid_raw_material.source.RawMaterialsDatabase
import com.checkpoint.rfid_raw_material.source.db.Provider
import com.checkpoint.rfid_raw_material.source.db.Tags
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class ItemsReadViewModel(application: Application) : AndroidViewModel(application)
{
    private var repository: DataRepository
    private var localSharedPreferences: LocalPreferences = LocalPreferences(application)

    init {
        repository = DataRepository.getInstance(
            RawMaterialsDatabase.getDatabase(application.baseContext)
        )
    }


    fun getTagsList(readNumber: Int): LiveData<List<Tags>> {
        return repository.getTagsListLive(readNumber)
    }

    fun getReadNumber():Int{
        return localSharedPreferences.getReadNumber()
    }





}