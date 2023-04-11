package com.checkpoint.rfid_raw_material

import com.checkpoint.rfid_raw_material.security.jwt.JWTDecoder
import org.junit.Assert
import org.junit.Test

class SecurityTest {
    val token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjMwLCJkZXZpY2UiOiIzZTRhY2M0ZjVhOWI0YWRhIn0.DAB-TRIOEZy1YLqlI917TEBNQCtqMe0lPWzOzFd3Mew"

    @Test
    fun isTokenValid(){
        val decoder = JWTDecoder(token)

        var isTokenValid: Boolean = decoder.decode().isEmpty()
        Assert.assertEquals(true,isTokenValid)

    }

}