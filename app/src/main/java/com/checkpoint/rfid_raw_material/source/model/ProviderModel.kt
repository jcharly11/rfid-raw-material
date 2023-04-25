package com.checkpoint.rfid_raw_material.source.model


data class ProviderModel (
    var id:Int = 0,
    var idAS: String? = null,
    var name: String? = null
) {
    override fun toString(): String {
        return name!!
    }
}
