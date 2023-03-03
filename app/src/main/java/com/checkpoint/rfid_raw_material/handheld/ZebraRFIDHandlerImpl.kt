package com.checkpoint.rfid_raw_material.handheld

import android.content.Context

class ZebraRFIDHandlerImpl {

    private var zebraRfidHandler = ZebraRFIDHandler()
    fun start(context: Context, maxPower: Int, nameDevice: String, session: String){
        zebraRfidHandler.onCreate(context,maxPower,nameDevice,session)
    }
    fun listener(responseHandlerInterface: ResponseHandlerInterface,
                 batteryHandlerInterface: BatteryHandlerInterface){
        zebraRfidHandler.setListener(responseHandlerInterface)
        zebraRfidHandler.setBatterListener(batteryHandlerInterface)
    }


    fun powerSoupportedList(): IntArray? {
        val list = zebraRfidHandler.powerSoupportedList()
        return list

    }

    fun onPostResume() {

        zebraRfidHandler.onResume()
    }
    fun currentPower(): Int{
        return zebraRfidHandler.currentPower()
    }

    fun onDestroy() {
        zebraRfidHandler.onDestroy()
    }

    fun perform(){
        zebraRfidHandler.performInventory()
    }
    fun stop(){
        zebraRfidHandler.stopInventory()
    }
    fun battery(){

        zebraRfidHandler.batteryLevel()
    }

}