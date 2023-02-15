package com.checkpoint.rfid_raw_material.model

import java.io.Serializable

data class InventoryItem (
    val id: Int,
    val item: String
) : Serializable