package com.checkpoint.rfid_raw_material

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.checkpoint.rfid_raw_material.bluetooth.BluetoothHandler
import com.checkpoint.rfid_raw_material.handheld.kt.DeviceInstanceBARCODE
import com.checkpoint.rfid_raw_material.handheld.kt.DeviceInstanceRFID
import com.checkpoint.rfid_raw_material.preferences.LocalPreferences
import com.checkpoint.rfid_raw_material.source.DataRepository
import com.checkpoint.rfid_raw_material.source.RawMaterialsDatabase
import com.checkpoint.rfid_raw_material.utils.dialogs.DialogErrorDeviceConnected
import com.checkpoint.rfid_raw_material.utils.dialogs.DialogLookingForDevice

open class ActivityBase(): AppCompatActivity() {
    var context: Context ?= null
    var localSharedPreferences: LocalPreferences?= null
    var repository: DataRepository?= null
    var dialogErrorDeviceConnected: DialogErrorDeviceConnected?= null
    var dialogLookingForDevice: DialogLookingForDevice? = null
    var bluetoothHandler: BluetoothHandler?= null
    var deviceInstanceRFID: DeviceInstanceRFID? = null
    var deviceInstanceBARCODE: DeviceInstanceBARCODE? = null


}