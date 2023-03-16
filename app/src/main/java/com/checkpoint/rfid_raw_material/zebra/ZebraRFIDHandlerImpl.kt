package com.checkpoint.rfid_raw_material.zebra

import android.content.Context
import com.zebra.rfid.api3.ENUM_TRIGGER_MODE

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
         zebraRfidHandler.performWriteTag("","","")
    }
    fun stop(){
        zebraRfidHandler.stopInventory()
    }
    fun battery(){

        zebraRfidHandler.batteryLevel()
    }

    fun write(tid: String, epc: String, pass: String){

        zebraRfidHandler.performWriteTag(tid, epc, pass)
    }

    fun mode(mode: Int){
        when(mode){
            0-> zebraRfidHandler.switchMode(ENUM_TRIGGER_MODE.RFID_MODE)
            1-> zebraRfidHandler.switchMode(ENUM_TRIGGER_MODE.BARCODE_MODE)
        }
    }
}