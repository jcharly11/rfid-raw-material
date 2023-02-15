package com.checkpoint.rfid_raw_material.model

import java.io.Serializable

data class ProviderItem(
    val id: Int,
    val idAS: Int,
    val name: String,
    val contact: String,
    val country: CountryItem
) : Serializable