package com.checkpoint.rfid_raw_material.ui.selection

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.checkpoint.rfid_raw_material.preferences.LocalPreferences

class OptionsWriteViewModel(application: Application) : AndroidViewModel(application){

    private var readNumber: Int = 0
    private var localSharedPreferences: LocalPreferences = LocalPreferences(application)


    fun setFragment(fragment:String) {

        localSharedPreferences!!.saveFragment(fragment)
    }

}