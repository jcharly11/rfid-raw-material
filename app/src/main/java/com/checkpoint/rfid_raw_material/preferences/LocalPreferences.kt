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
    private var flagSessionValue:String? = null

    private var selectedLanguageValue:String? = null
    private var readNumber:String? = null
    private var tokenLicense:String? = null

    init{
        sharedPreferences = application.getSharedPreferences("config_device", Context.MODE_PRIVATE)
        flagPowerValue= application.resources.getString(R.string.power_config_settings)
        flagPauseValue= "pause"
        flagSessionValue= "session"

        selectedLanguageValue="language"
        readNumber="0"
        tokenLicense=""
    }

    fun saveMaxToPreferences(maxPower : Int){
        return with (sharedPreferences.edit()) {
            putInt(flagPowerValue, maxPower)
            apply()
        }
    }

    fun getMaxFromPreferences():Int{
        val mp  = sharedPreferences.getInt(flagPowerValue,150)
        Log.e("---getMaxFromPreferences-->",""+mp)

         return mp
    }

    fun saveSessionToPreferences(session : String){
        return with (sharedPreferences.edit()) {
            putString(flagSessionValue, session)
            apply()
        }
    }

    fun getSessionFromPreferences():String{
       val ss  = sharedPreferences.getString(flagSessionValue,"SESSION_1")!!
        if (ss.isEmpty()){}

        Log.e("---getSessionFromPreferences-->",""+ss)

        return ss
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


    fun saveReadNumber(readNumb : Int){
        return with (sharedPreferences.edit()) {
            putInt(readNumber,readNumb)
            apply()
        }
    }

    fun getReadNumber():Int{
        return sharedPreferences.getInt(readNumber,0)
    }

    fun setTokenLicense(token: String){
        return with (sharedPreferences.edit()) {
            putString(tokenLicense, token)
            apply()
        }
    }


    fun getLicenseToken():String{
        return sharedPreferences.getString(tokenLicense,"")!!
    }

}