package com.checkpoint.rfid_raw_material.source.model

data class LanguageModel (
    var namelang: String? = null,
    var lang: String? = null

){
    override fun toString(): String {
        return namelang!!
    }
}