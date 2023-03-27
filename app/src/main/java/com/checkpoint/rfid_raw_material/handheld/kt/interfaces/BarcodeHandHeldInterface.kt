package com.checkpoint.rfid_raw_material.handheld.kt.interfaces

interface BarcodeHandHeldInterface {
    fun setDataBarCode(code: String)
    fun connected(status: Boolean)
}