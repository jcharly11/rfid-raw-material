package com.checkpoint.rfid_raw_material.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.util.Log

class BluetoothHandler(context: Context) {
    private  var bluetoothManager: BluetoothManager
    private  var bluetoothAdapter: BluetoothAdapter

    init {

        bluetoothManager = context.getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager.adapter

    }


    fun validate():Boolean{


        return bluetoothAdapter.isEnabled
    }

    fun state():Int{
        return   bluetoothAdapter.state
    }

    fun list(): MutableSet<BluetoothDevice>? {
        return bluetoothAdapter.bondedDevices

    }

    fun discovey() {
        val finish =
            bluetoothAdapter.startDiscovery()
        Log.e("Dsicovery result","$finish")
    }

    fun stopDiscovey(){
        bluetoothAdapter.cancelDiscovery()
    }

    fun pair(device: BluetoothDevice):Boolean{
        return device.createBond()

    }


}