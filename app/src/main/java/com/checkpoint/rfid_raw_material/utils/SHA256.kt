package com.checkpoint.rfid_raw_material.utils

import java.security.MessageDigest

class SHA256 {

    fun hash(): String {
            val bytes = this.toString().toByteArray()
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            return digest.fold("") { str, it -> str + "%02x".format(it) }
    }


}