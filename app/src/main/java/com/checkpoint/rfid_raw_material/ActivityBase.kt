package com.checkpoint.rfid_raw_material

import android.Manifest
import android.content.Context
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
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
    var dialogErrorDeviceConnected: DialogErrorDeviceConnected?= null
    var dialogSelectPairDevices: DialogSelectPairDevices? = null
    var bluetoothHandler: BluetoothHandler?= null
    var deviceInstanceRFID: DeviceInstanceRFID? = null
    var deviceInstanceBARCODE: DeviceInstanceBARCODE? = null
    var btnHandHeldGun: AppCompatImageView? = null
    var batteryView: CustomBattery? = null
    var btnCreateLog: AppCompatImageView? = null
    var lyCreateLog: LinearLayout? = null
    var deviceName: String = String()

    internal val requestPermissions12 by lazy {
        permissionsBuilder(
            Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_ADVERTISE
        ).build()
    }
    internal val requestPermissions11 by lazy {
        permissionsBuilder(
            Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_ADVERTISE, Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        ).build()
    }


}