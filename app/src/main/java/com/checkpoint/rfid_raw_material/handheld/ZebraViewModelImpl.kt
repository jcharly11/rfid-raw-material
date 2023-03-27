package com.checkpoint.rfid_raw_material.handheld

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.checkpoint.rfid_raw_material.handheld.kt.interfaces.BatteryHandlerInterface
import com.checkpoint.rfid_raw_material.handheld.kt.interfaces.ResponseHandlerInterface
import com.zebra.rfid.api3.TagData

class ZebraViewModelImpl(application: Application) : AndroidViewModel(application),
    ResponseHandlerInterface,
    BatteryHandlerInterface {
    override fun batteryLevel(level: Int) {
        TODO("Not yet implemented")
    }

    override fun handleTagdata(tagData: Array<TagData?>?) {
        TODO("Not yet implemented")
    }

    override fun handleTriggerPress(pressed: Boolean) {
        TODO("Not yet implemented")
    }

    override fun handleStartConnect(connected: Boolean) {
        TODO("Not yet implemented")
    }


}