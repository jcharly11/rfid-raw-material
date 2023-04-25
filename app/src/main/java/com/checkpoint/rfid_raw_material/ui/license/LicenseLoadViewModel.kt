package com.checkpoint.rfid_raw_material.ui.license

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.checkpoint.rfid_raw_material.preferences.LocalPreferences
import com.checkpoint.rfid_raw_material.security.IdentifierDevice
import com.checkpoint.rfid_raw_material.security.jwt.JWTDecoder
import io.sentry.Sentry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject



class LicenseLoadViewModel(application: Application) : AndroidViewModel(application) {

    var context = application.baseContext
    private var localSharedPreferences: LocalPreferences = LocalPreferences(application)
    private val _idDevice: MutableLiveData<String> = MutableLiveData("")
    val idDevice: LiveData<String> = _idDevice
    init {

        _idDevice.postValue(IdentifierDevice(context)!!.getIdentifier())
    }
    fun copyToClpBoard(textToCopy: String){

        val clipboardManager = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", textToCopy)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(context, "Id copied to clipboard", Toast.LENGTH_LONG).show()
    }

    suspend fun validateLicense(tokenLicense: String) : Boolean= withContext(Dispatchers.Main){

        try {

            val decoder = JWTDecoder(tokenLicense)
            var data: String = decoder.decode()
            Log.e("decoder.decode():",data)
            val obj = JSONObject(data)
            Log.e("decoder.decode():",obj.getString("exp"))
            Log.e("decoder.decode():",obj.getString("device"))

            //TODO guardar a shared preferences el token

            true
        }catch (ex: Exception){
            Sentry.captureMessage("${ex.message}")
            false
        }
     }

    fun getTokenLicense():String{
        return localSharedPreferences.getLicenseToken()
    }

    fun setTokenLicense(token: String){
        return localSharedPreferences.setTokenLicense(token)
    }

}