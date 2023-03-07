package com.checkpoint.rfid_raw_material

import com.checkpoint.rfid_raw_material.utils.Conversor
import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ConversionTest {
    @Test
    fun conversion_isCorrect() {

        val conversor =  Conversor()
        var hexValueEpc = ""
        var version = conversor.toBinaryString("18",5,'0')
        var subVersion = conversor.toBinaryString("0",5,'0')
        var type = conversor.toBinaryString("4",6,'0')
        var supplier = conversor.toBinaryString("123456",32,'0')
        var piece = conversor.toBinaryString("123456789012345678901234",80,'0')

        var binaryChain = "$version$type$subVersion$piece$supplier"
        var binaryGroup = binaryChain.chunked(4)
        binaryGroup.iterator().forEach {
            hexValueEpc += conversor.toHexadecimalString(it)

        }

        println(hexValueEpc)

        assertEquals("90801A249B1F10A06C96AFF20001E240",hexValueEpc)
    }
}