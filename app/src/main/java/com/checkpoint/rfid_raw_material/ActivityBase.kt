package com.checkpoint.rfid_raw_material

import android.Manifest
import android.content.Context
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.navigation.ui.AppBarConfiguration
import com.checkpoint.rfid_raw_material.bluetooth.BluetoothHandler
import com.checkpoint.rfid_raw_material.handheld.kt.DeviceInstanceBARCODE
import com.checkpoint.rfid_raw_material.handheld.kt.DeviceInstanceRFID
import com.checkpoint.rfid_raw_material.preferences.LocalPreferences
import com.checkpoint.rfid_raw_material.source.DataRepository
import com.checkpoint.rfid_raw_material.source.RawMaterialsDatabase
import com.checkpoint.rfid_raw_material.utils.CustomBattery
import com.checkpoint.rfid_raw_material.utils.dialogs.DialogErrorDeviceConnected
import com.checkpoint.rfid_raw_material.utils.dialogs.DialogLookingForDevice
import com.checkpoint.rfid_raw_material.utils.dialogs.DialogSelectPairDevices
import com.fondesa.kpermissions.extension.permissionsBuilder

open class ActivityBase(): AppCompatActivity() {
    var context: Context ?= null
    var localSharedPreferences: LocalPreferences?= null
    var repository: DataRepository?= null
    lateinit var appBarConfiguration: AppBarConfiguration

    internal val requestPermissions12 by lazy {
        permissionsBuilder(
            Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).build()
    }
    internal val requestPermissions11 by lazy {
        permissionsBuilder(
            Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_ADVERTISE, Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE
        ).build()
    }


}