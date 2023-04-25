package com.checkpoint.rfid_raw_material.security

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
//import io.sentry.Sentry

class IdentifierDevice(private val context: Context){

    @SuppressLint("HardwareIds")
    fun getIdentifier(): String? {
        return try {
              Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            )


        } catch (ex: Exception) {
            //Sentry.captureMessage("${ex.message}")
            ""
        }
    }
}