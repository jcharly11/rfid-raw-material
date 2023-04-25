package com.checkpoint.rfid_raw_material

import com.checkpoint.rfid_raw_material.security.jwt.JWTDecoder
import org.junit.Assert
import org.junit.Test

class SecurityTest {
    val token = ""

    @Test
    fun isTokenValid(){
        val decoder = JWTDecoder(token)

        var isTokenValid: Boolean = decoder.decode().isEmpty()
        Assert.assertEquals(true,isTokenValid)

    }

}