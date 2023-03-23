package com.checkpoint.rfid_raw_material.ui.configuration

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.checkpoint.rfid_raw_material.enums.LanguageOptions
import com.checkpoint.rfid_raw_material.enums.TypeInventory
import com.checkpoint.rfid_raw_material.preferences.LocalPreferences
import com.checkpoint.rfid_raw_material.source.DataRepository
import com.checkpoint.rfid_raw_material.source.RawMaterialsDatabase
import com.checkpoint.rfid_raw_material.source.db.Language
import com.checkpoint.rfid_raw_material.source.db.Provider
import com.checkpoint.rfid_raw_material.source.model.LanguageModel
import com.checkpoint.rfid_raw_material.source.model.ProviderModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.reflect.Type

class ConfigurationViewModel(application: Application) : AndroidViewModel(application) {
    private var repository: DataRepository
    private var localSharedPreferences: LocalPreferences = LocalPreferences(application)

    init {
        repository = DataRepository.getInstance(
            RawMaterialsDatabase.getDatabase(application.baseContext)
        )
    }

    suspend fun getLanguageList():MutableList<LanguageModel> = withContext(Dispatchers.IO){
        val list= repository.getLanguages()
        var listLanguage:MutableList<LanguageModel> = mutableListOf()

        list.iterator().forEachRemaining {
            var itemLang= LanguageModel(it.language, it.lang)
            listLanguage!!.add(itemLang)
        }
        listLanguage= listLanguage!!.toMutableList()
        listLanguage
    }

    suspend fun insertLanguages() = withContext(Dispatchers.IO) {
        /*var language: Language? = null
        for (item in LanguageOptions.values()) {
            repository.insertNewLang(
                language = Language(
                    0,
                    item.name,
                    item.getDisplayLang().toString()
                )
            )
        }*/
        repository.insertNewLang(Language(0,"Spanish","es"))
        repository.insertNewLang(Language(0,"English","en"))
    }

    fun setLanguage(language: String){
        localSharedPreferences.setSelectedLanguage(language)

    }
}