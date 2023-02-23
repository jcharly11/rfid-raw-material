package com.checkpoint.rfid_raw_material.zebra

import com.zebra.rfid.api3.TagData

interface ResponseHandlerInterface {
    fun handleTagdata(tagData: Array<TagData?>?)
    fun handleTriggerPress(pressed: Boolean)
    fun handleStartConnect(connected: Boolean)
}