package com.checkpoint.rfid_raw_material.handheld.kt

import android.content.Context
import com.checkpoint.rfid_raw_material.handheld.BatteryHandlerInterface
import com.checkpoint.rfid_raw_material.handheld.ResponseHandlerInterface
import com.zebra.rfid.api3.*

interface HandHeldDevice {
    suspend fun instance(context: Context?,device: DeviceConfig?)
    suspend fun resume()
    suspend fun perform()
    suspend fun stop()
    suspend fun disconnect()

}

abstract class ZebraReader8500(): HandHeldDevice {
    internal lateinit var mode: ENUM_TRIGGER_MODE
    internal lateinit var readers: Readers
    internal lateinit var availableRFIDReaderList: ArrayList<ReaderDevice>
    internal lateinit var readerDevice: ReaderDevice
    internal lateinit var reader: RFIDReader
    internal  var triggerInfo: TriggerInfo = TriggerInfo()
    internal var handheldTrigger:HandheldTrigger =HandheldTrigger()
    internal lateinit var responseHandlerInterface: ResponseHandlerInterface
    internal lateinit var batteryHandlerInterface: BatteryHandlerInterface

}

