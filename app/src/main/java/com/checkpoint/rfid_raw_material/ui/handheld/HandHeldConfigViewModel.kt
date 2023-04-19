package com.checkpoint.rfid_raw_material.ui.handheld

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.checkpoint.rfid_raw_material.preferences.LocalPreferences

class HandHeldConfigViewModel(application: Application) : AndroidViewModel(application) {

    private var  localPreferences = LocalPreferences(application)


    fun seveConfigToPreferences(sessionSelected: String, maxPower: Int) {
        localPreferences.saveMaxToPreferences(maxPower)
        localPreferences.saveSessionToPreferences(sessionSelected)
    }

}