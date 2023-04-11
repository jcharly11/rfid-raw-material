package com.checkpoint.rfid_raw_material

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.fondesa.kpermissions.*
import com.fondesa.kpermissions.coroutines.flow
import com.fondesa.kpermissions.coroutines.sendSuspend
import com.fondesa.kpermissions.extension.liveData
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.request.PermissionRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity(), PermissionRequest.Listener {
    private val request by lazy {
        permissionsBuilder(Manifest.permission.BLUETOOTH_CONNECT,Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_SCAN,Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        request.addListener(this)

        request.send()
    }

    override fun onPermissionsResult(result: List<PermissionStatus>) {
        var res:Int= 0

        result.iterator().forEachRemaining{
            if(it.isGranted()==true) {
                Log.d(it.permission.toString(),"aceptado")
                res++
            }
            else if(it.isDenied()) {
                Log.d(it.permission.toString(),"denegado")
            }
        }

        if(res>= 4){
            Toast.makeText(this, "todos los permisos", Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(this, "faltan permisos", Toast.LENGTH_SHORT).show()
        }
    }
}