package com.checkpoint.rfid_raw_material

import android.Manifest
import android.content.Context
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.navigation.ui.AppBarConfiguration
import com.checkpoint.rfid_raw_material.bluetooth.BluetoothHandler
import com.checkpoint.rfid_raw_material.handheld.kt.Device
import com.checkpoint.rfid_raw_material.handheld.kt.DeviceInstanceBARCODE
import com.checkpoint.rfid_raw_material.handheld.kt.DeviceInstanceRFID
import com.checkpoint.rfid_raw_material.preferences.LocalPreferences
import com.checkpoint.rfid_raw_material.source.DataRepository
import com.checkpoint.rfid_raw_material.source.RawMaterialsDatabase
import com.checkpoint.rfid_raw_material.utils.CustomBattery
import com.checkpoint.rfid_raw_material.utils.LogCreator
import com.checkpoint.rfid_raw_material.utils.dialogs.DialogErrorDeviceConnected
import com.checkpoint.rfid_raw_material.utils.dialogs.DialogLookingForDevice
import com.checkpoint.rfid_raw_material.utils.dialogs.DialogSelectPairDevices
import com.fondesa.kpermissions.extension.permissionsBuilder

open class ActivityBase(): AppCompatActivity() {

    var device: Device? = null
    var context: Context ?= null
    var localSharedPreferences: LocalPreferences?= null
    var btnHandHeldGun: AppCompatImageView? = null
    var batteryView: CustomBattery? = null
    var btnCreateLog: AppCompatImageView? = null
    var lyCreateLog: LinearLayout? = null

    lateinit var appBarConfiguration: AppBarConfiguration

    var dialogLookingForDevice: DialogLookingForDevice? = null
    var dialogErrorDeviceConnected: DialogErrorDeviceConnected? = null

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

    fun loadFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()
    }

    fun removeFragment(fragment: Fragment){
        val fragmentTransaction = supportFragmentManager.beginTransaction()
         fragmentTransaction.remove(fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
    suspend fun logs(type: String, repository: DataRepository,context: Context,readNumber: Int){
        var listScanned = repository.getTagsList(readNumber!!)
        if (listScanned.size > 0) {
            var logCreator = LogCreator(context)
            var tagList = repository.getTagsListForLogs(readNumber)
            logCreator.createLog(type, tagList)
            }
        }

}