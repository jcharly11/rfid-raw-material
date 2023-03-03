package com.checkpoint.rfid_raw_material.preferences

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.checkpoint.rfid_raw_material.R

class LocalPreferences(application: Application) {
    private var sharedPreferences: SharedPreferences
    private var flagPowerValue:String? = null
    private var flagPauseValue:String? = null
    private var selectedLanguageValue:String? = null

    init{
        sharedPreferences = application.getSharedPreferences("config_device", Context.MODE_PRIVATE)
        flagPowerValue= application.resources.getString(R.string.power_config_settings)
        flagPauseValue= "pause"
        selectedLanguageValue="language"

    }

    fun saveMaxToPreferences(maxPower : Int){
        return with (sharedPreferences.edit()) {
            putInt(flagPowerValue, maxPower)
            apply()
        }
    }

    fun getMaxFromPreferences():Int{
        return sharedPreferences.getInt(flagPowerValue,10)
    }

    fun setPauseStatus(status: Boolean){
        return with (sharedPreferences.edit()) {
            putBoolean(flagPauseValue, status)
            apply()
            Log.e("---setPauseStatus-->",""+sharedPreferences.getBoolean(flagPauseValue,false))
        }


    }
    fun getPauseStatus(): Boolean{
        return sharedPreferences.getBoolean(flagPauseValue,false)

    }
    fun setSelectedLanguage(language: String){
        return with (sharedPreferences.edit()) {
            putString(selectedLanguageValue, language)
            apply()
        }
    }
    fun getSelectedLanguage(): String{
        return sharedPreferences.getString(selectedLanguageValue,"")!!
    }
}