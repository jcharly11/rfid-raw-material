package com.checkpoint.rfid_raw_material.security.jwt

import com.auth0.android.jwt.JWT
import io.jsonwebtoken.Jwts


class JWTDecoder(private val token: String) {


    fun decode(): String {
        return Jwts.parserBuilder()
            .setSigningKey("Y2Y2MTI3OTVjMTQxYmFhZTczNGJiMjgxZDcwMTM5NTUwMTZlNmYxZjE5MTY2NzkyZGU3YTAzYmQzNGNhOTUyZg==")
            // TODO: Get from native opcode
            .build()
            .parseClaimsJws(token).body.toString()
    }

}