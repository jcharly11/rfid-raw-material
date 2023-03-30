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



        var reverse =  Reverse()
        var binaryString = reverse.hexadecimalToBinaryString("90801A249B1F10A06C96AFF20001E240")

        "$version$type$subVersion$piece$supplier"
         supplier = binaryString.subSequence(binaryString.length-32,binaryString.length).toString()
         piece = binaryString.subSequence(binaryString.length- (32+80),binaryString.length-32).toString()

         subVersion = binaryString.subSequence(binaryString.length- (32+80+5),binaryString.length-(32+80)).toString()
         type = binaryString.subSequence(binaryString.length- (32+80+5+6),binaryString.length-(32+80+5)).toString()

        println("supplier:$supplier")
        println( "supplier:" + supplier.toBigInteger(2))

        println("piece:$piece")
        println( "piece:" + piece.toBigInteger(2))

        println("type:$type")
        println("type:" + type.toBigInteger(2))

        println("subVersion:$subVersion")
        println("subVersion:" + subVersion.toBigInteger(2))


        Assert.assertNotEquals("123456",supplier.toInt(2))

    }


}