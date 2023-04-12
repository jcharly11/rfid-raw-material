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
import com.checkpoint.rfid_raw_material.security.IdentifierDevice
import com.checkpoint.rfid_raw_material.security.jwt.JWTDecoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject





class LicenseLoadViewModel(application: Application) : AndroidViewModel(application) {

    var context = application.baseContext

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


            true
        }catch (ex: Exception){
            false
        }
     }


}