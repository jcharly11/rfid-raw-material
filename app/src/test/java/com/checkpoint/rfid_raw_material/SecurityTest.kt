package com.checkpoint.rfid_raw_material

import com.checkpoint.rfid_raw_material.security.jwt.JWTDecoder
import org.junit.Assert
import org.junit.Test

class SecurityTest {
    val token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6IjNlNGFjYzRmNWE5YjRhZGEiLCJleHAiOjMwfQ.EcgnAVsEpah1TEeqbIjtZ6_JQ-bVjkrueC_PuG_FgLY"

    @Test
    fun isTokenValid(){
        val decoder = JWTDecoder(token)
        var isTokenValid: Boolean
        val idDevice = decoder.decodeId()
        val exp =  decoder.decodeExp()
        println("idDevice:$idDevice")
        println("exp:$exp")

        isTokenValid = idDevice.isEmpty()
        Assert.assertEquals(true,isTokenValid)

    }

}