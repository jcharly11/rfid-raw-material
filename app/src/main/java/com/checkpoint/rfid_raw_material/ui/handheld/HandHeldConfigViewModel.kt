package com.checkpoint.rfid_raw_material.ui.handheld

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.checkpoint.rfid_raw_material.preferences.LocalPreferences

class HandHeldConfigViewModel(application: Application) : AndroidViewModel(application) {

    private var  localPreferences = LocalPreferences(application)


    fun saveConfigToPreferences(sessionSelected: String, maxPower: Int) {
        localPreferences.saveMaxToPreferences(maxPower)
        localPreferences.saveSessionToPreferences(sessionSelected)
    }

    fun getConfigFromPreferences():Pair<Int, String>{

        return Pair(localPreferences.getMaxFromPreferences(),
            localPreferences.getSessionFromPreferences())

    }
}