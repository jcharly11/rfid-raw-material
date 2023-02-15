package com.checkpoint.rfid_raw_material

import org.junit.Assert
import org.junit.Test

class DecToBinToHexTest {

    @Test
    fun convert(version:  Int,type: Int, subVersion: Int, brand: Int, identifier: Int ) {
        val bVersion = Integer.toBinaryString(version)
        val bSubVersion = Integer.toBinaryString(subVersion)
        val bType = Integer.toBinaryString(type)
        val bBrand = Integer.toBinaryString(brand)
        val bIndentifier = Integer.toBinaryString(identifier)

        val longBinaryString = "$bVersion$bSubVersion$bType$bBrand$bIndentifier"

        Assert.assertEquals(4, 2 + 2)
    }
}