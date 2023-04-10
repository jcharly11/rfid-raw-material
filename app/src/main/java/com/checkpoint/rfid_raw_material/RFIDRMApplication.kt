package com.checkpoint.rfid_raw_material

import android.app.Application
import com.checkpoint.rfid_raw_material.security.IdentifierDevice

class RFIDRMApplication: Application() {


    companion object{
        private var  identifierDevice:  IdentifierDevice? = null
        var  id: String? = null

    }

    override fun onCreate() {
        super.onCreate()
        identifierDevice = IdentifierDevice(applicationContext)
        id = identifierDevice!!.getIdentifier()
    }

}