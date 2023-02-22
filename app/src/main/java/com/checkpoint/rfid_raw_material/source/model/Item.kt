package com.checkpoint.rfid_raw_material.source.model

import java.io.Serializable


data class Item (

    val id: Int,
    val itemName: String
) : Serializable