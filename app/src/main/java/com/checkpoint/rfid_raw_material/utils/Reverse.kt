package com.checkpoint.rfid_raw_material.utils

import android.util.Log

class Reverse {


    fun hexadecimalToBinaryString(hexadecimalValue: String): String{
        var binaryString = String()
        hexadecimalValue.toCharArray().iterator().forEachRemaining{

            binaryString +=  hexToBinary(it.toString())
        }
        return binaryString

    }

    fun hexToBinary(value: String): String{
        var result = String()
        when(value){
            "0" ->{ result = "0000" }
            "1"->{ result =  "0001"}
            "2"->{ result = "0010" }
            "3"->{ result = "0011" }
            "4"->{ result = "0100" }
            "5",->{ result = "0101" }
            "6"->{ result = "0110" }
            "7"->{ result = "0111" }
            "8"->{ result = "1000" }
            "9"->{ result = "1001" }
            "A"->{ result = "1010" }
            "B"->{ result = "1011" }
            "C"->{ result = "1100" }
            "D"->{ result = "1101" }
            "E"->{ result = "1110" }
            "F"->{ result = "1111" }

        }
        return result
    }


}