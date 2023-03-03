package com.checkpoint.rfid_raw_material.source.model

import java.io.Serializable

class Logs(
    val date: String,
    val epc: String,
    val version: String,
    val type: String,
    val subversion: String,
    val identifier: String,
    val suplier: String
)