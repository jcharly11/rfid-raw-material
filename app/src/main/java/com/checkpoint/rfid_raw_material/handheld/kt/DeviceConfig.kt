package com.checkpoint.rfid_raw_material.handheld.kt

import com.zebra.rfid.api3.ENUM_TRANSPORT
import com.zebra.rfid.api3.ENUM_TRIGGER_MODE
import com.zebra.rfid.api3.SESSION
import java.io.Serializable

data class DeviceConfig (

    val maxPower: Int,
    val session: SESSION,
    val device: String,
    val mode: ENUM_TRIGGER_MODE,
    val type: ENUM_TRANSPORT
) : Serializable
