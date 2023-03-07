package com.checkpoint.rfid_raw_material.zebra

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


    //Soupported list power level
    fun list(): IntArray? {
        val list = zebraRfidHandler.powerSoupportedList()
        return list

    }

    fun resume() {

        zebraRfidHandler.onResume()
    }
    fun power(): Int{
        return zebraRfidHandler.currentPower()
    }

    fun destroy() {
        zebraRfidHandler.onDestroy()
    }

    fun perform(){
        zebraRfidHandler.performInventory()
    }

    fun performWriteTag(){
        zebraRfidHandler.performWriteTag()
    }
    fun stop(){
        zebraRfidHandler.stopInventory()
    }
    fun battery(){

        zebraRfidHandler.batteryLevel()
    }
    fun write(){

    }
}