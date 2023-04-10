package com.checkpoint.rfid_raw_material.security.jwt

import com.auth0.android.jwt.JWT




class JWTDecoder(private val token: String) {

    var jwt = JWT(token)

    fun decodeId(): String {
        return jwt.claims["id"].toString()
    }

    fun decodeExp(): String {
        return jwt.claims["exp"].toString()
    }
}