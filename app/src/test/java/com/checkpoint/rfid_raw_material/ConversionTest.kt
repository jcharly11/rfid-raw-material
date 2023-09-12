package com.checkpoint.rfid_raw_material

import com.checkpoint.rfid_raw_material.utils.Conversor
import org.junit.Assert.*
import org.junit.Test


class ConversionTest {
    @Test
    fun conversion_isCorrect() {

        val conversion =  Conversor()

        var version = conversion.toBinaryString("18",5,'0')
        var subVersion = conversion.toBinaryString("0",5,'0')
        var type = conversion.toBinaryString("4",6,'0')
        var supplier = conversion.toBinaryString("123456",32,'0')
        var piece = conversion.toBinaryString("123456789012345678901234",80,'0')

        var decimalString = "$version$type$subVersion$piece$supplier"

        var binaryChain=conversion.stringBinaryPadding(arrayOf(version,type,subVersion,piece,supplier))
        var hexValueEpc = conversion.groupBytes(binaryChain)
        println(hexValueEpc)
        println(hexValueEpc.encodeToByteArray().size*4)

        assertEquals("90801A249B1F10A06C96AFF20001E240",hexValueEpc)
    }
}