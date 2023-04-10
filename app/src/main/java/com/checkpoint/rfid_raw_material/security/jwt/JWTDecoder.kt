package com.checkpoint.rfid_raw_material.security.jwt

import com.auth0.android.jwt.JWT
import io.jsonwebtoken.Jwts


class JWTDecoder(private val token: String) {


    fun decode(): String {
        return Jwts.parserBuilder()
            .setSigningKey("c6kNFvjUCGDVetfV")
            .build()
            .parseClaimsJws(token).body["id"].toString()
    }

}