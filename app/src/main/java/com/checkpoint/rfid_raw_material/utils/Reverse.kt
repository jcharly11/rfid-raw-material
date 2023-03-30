package com.checkpoint.rfid_raw_material.utils

import android.util.Log
import com.checkpoint.rfid_raw_material.pojos.ConfigLongValues

class Reverse {
    val longValues = ConfigLongValues()
    var binaryString = String()


    fun hexadecimalToBinaryString(hexadecimalValue: String): Boolean{
         hexadecimalValue.toCharArray().iterator().forEachRemaining{
            binaryString +=  hexToBinary(it.toString())
        }

        return binaryString.isNotEmpty()
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

    fun getSupplier(): String{
        return binaryString.subSequence(binaryString.length-longValues.supplierLong,binaryString.length
        ).toString().toBigInteger(2).toString()
    }
    fun getPiece(): String{
        return binaryString.subSequence(binaryString.length- (longValues.supplierLong+longValues.pieceLong),binaryString.length-longValues.supplierLong
        ).toString().toBigInteger(2).toString()
    }
    fun getSubVersion():String{
        return binaryString.subSequence(binaryString.length- (longValues.supplierLong+longValues.pieceLong+longValues.subVersionLong),binaryString.length-(longValues.supplierLong+longValues.pieceLong)
        ).toString().toBigInteger(2).toString()

    }

    fun getType():String{
        return binaryString.subSequence(binaryString.length- (longValues.supplierLong+longValues.pieceLong+longValues.subVersionLong+longValues.typeLong),binaryString.length-(longValues.supplierLong+longValues.pieceLong+longValues.subVersionLong)
        ).toString().toBigInteger(2).toString()
    }

    fun getVersion():String{
        return binaryString.subSequence(binaryString.length- (longValues.supplierLong+longValues.pieceLong+longValues.subVersionLong+longValues.typeLong+longValues.versionLong),binaryString.length-(longValues.supplierLong+longValues.pieceLong+longValues.subVersionLong+longValues.typeLong)
        ).toString().toBigInteger(2).toString()

    }
}