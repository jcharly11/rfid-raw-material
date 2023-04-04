package com.checkpoint.rfid_raw_material

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.checkpoint.rfid_raw_material.handheld.kt.Device
import com.checkpoint.rfid_raw_material.handheld.kt.DeviceInstanceBARCODE
import com.checkpoint.rfid_raw_material.handheld.kt.interfaces.BarcodeHandHeldInterface
import com.checkpoint.rfid_raw_material.handheld.kt.interfaces.DeviceConnectStatusInterface

class BarCodeActivity : AppCompatActivity(), DeviceConnectStatusInterface,
    BarcodeHandHeldInterface {
    private lateinit var deviceInstanceBARCODE: DeviceInstanceBARCODE
    private lateinit var device: Device

    @RequiresApi(Build.VERSION_CODES.N)
    val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
            }
            permissions.getOrDefault(Manifest.permission.BLUETOOTH_SCAN, false) -> {
                // Only approximate location access granted.
            }
            permissions.getOrDefault(Manifest.permission.BLUETOOTH_ADMIN, false) -> {
                // Only approximate location access granted.
            }
            permissions.getOrDefault(Manifest.permission.BLUETOOTH_CONNECT, false) -> {
                // Only approximate location access granted.
            }
            else -> {
            // No location access granted.
        }
        }
    }
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bar_code)

        device = Device(applicationContext, "RFD850019078523021045", this)

        var btnBarcode = findViewById<Button>(R.id.btnBarcode)
        btnBarcode.setOnClickListener {
            deviceInstanceBARCODE.setBarCodeHandHeldInterface(this)
        }

        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_CONNECT
        ))

    }

    override fun onStart() {
        super.onStart()

        device.connect()

    }

    override fun onDestroy() {
        super.onDestroy()
        device.disconnect()
    }


    override fun isConnected(b: Boolean) {
        deviceInstanceBARCODE= DeviceInstanceBARCODE(device!!.getReaderDevice(),applicationContext)
    }

    override fun setDataBarCode(code: String) {
        Log.e("setDataBarCode","$code")
    }

    override fun connected(status: Boolean) {


        }


}