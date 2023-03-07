package com.checkpoint.rfid_raw_material.source.model


data class ProviderModel (
    var id:Int = 0,
    var name: String? = null
) {
    override fun toString(): String {
        return name!!
    }
}
