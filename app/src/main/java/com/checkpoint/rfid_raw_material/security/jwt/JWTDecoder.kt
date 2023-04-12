package com.checkpoint.rfid_raw_material.security.jwt

import android.util.Log
import io.jsonwebtoken.Jwts


class JWTDecoder(private val token: String) {


    fun decode(): String {
        Log.e("KEY FROM CPP","${getKey()}")
        return Jwts.parserBuilder()
            .setSigningKey("Y2Y2MTI3OTVjMTQxYmFhZTczNGJiMjgxZDcwMTM5NTUwMTZlNmYxZjE5MTY2NzkyZGU3YTAzYmQzNGNhOTUyZg==")
            // TODO: Get from native opcode
            .build()
            .parseClaimsJws(token).body.toString()
    }
    external fun getKey(): String

    companion object {
        // Used to load the 'sign-lib' library on application startup.
        init {
            System.loadLibrary("sign-lib")
        }
    }
}