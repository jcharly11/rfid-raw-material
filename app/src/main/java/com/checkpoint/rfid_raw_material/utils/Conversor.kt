package com.checkpoint.rfid_raw_material.utils

import java.math.BigInteger

class Conversor {

    fun toBinaryString(value : String,length: Int, padding: Char): String {

        var input = BigInteger(value)
        var salida = input.divideAndRemainder(BigInteger("2"))
        var div = salida[0]
        var res = salida[1]
        var binary=""
        if(res.compareTo(BigInteger("0") ) == 0){
            binary +="0"
        }else{
            binary += "1"
        }

        while(div > BigInteger("0")){

            salida= div.divideAndRemainder(BigInteger("2"))
            div = salida[0]
            res = salida[1]

            if(res.compareTo(BigInteger("0") ) == 0){
                binary +="0"
            }else{
                binary += "1"
            }

        }

        return binary.reversed().padStart(length,padding)

    }
    fun toHexadecimalString(value: String): String{
        var result: String =""
        when(value){
            "0000"->{ result = "0"  }
            "0001"->{ result = "1" }
            "0010"->{ result = "2" }
            "0011"->{ result = "3" }
            "0100"->{ result = "4" }
            "0101"->{ result = "5" }
            "0110"->{ result = "6" }
            "0111"->{ result = "7" }
            "1000"->{ result = "8" }
            "1001"->{ result = "9" }
            "1010"->{ result = "A" }
            "1011"->{ result = "B" }
            "1100"->{ result = "C" }
            "1101"->{ result = "D" }
            "1110"->{ result = "E" }
            "1111"->{ result = "F" }

        }
        return result
    }

}