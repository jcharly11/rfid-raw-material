package com.checkpoint.rfid_raw_material

import com.checkpoint.rfid_raw_material.utils.Conversor
import com.checkpoint.rfid_raw_material.utils.Reverse
import org.junit.Assert
import org.junit.Test


class ReverseTest {
    @Test
    fun isReverse_correct(){
        var version = String()// conversion.toBinaryString("18",5,'0')
        var subVersion = String()//conversion.toBinaryString("0",5,'0')
        var type = String()//conversion.toBinaryString("4",6,'0')
        var supplier = String()//conversion.toBinaryString("123456",32,'0')
        var piece = String()//conversion.toBinaryString("123456789012345678901234",80,'0')



        var reverse =  Reverse(null!!)
        reverse.hexadecimalToBinaryString("90801A249B1F10A06C96AFF20001E240")

        supplier = reverse.getSupplier()
        piece = reverse.getPiece()
        subVersion = reverse.getSubVersion()
        type =  reverse.getType()
        version =  reverse.getVersion()

        println("supplier:$supplier")
        println("piece:$piece")
        println("subVersion:$subVersion")
        println("type:$type")
        println("version:$version")


        Assert.assertEquals("123456",supplier)

    }


}