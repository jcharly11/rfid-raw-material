package com.checkpoint.rfid_raw_material.utils

import android.app.Application
import android.util.Log
import com.checkpoint.rfid_raw_material.pojos.ConfigLongValues
import com.checkpoint.rfid_raw_material.source.DataRepository
import com.checkpoint.rfid_raw_material.source.RawMaterialsDatabase
import io.sentry.Sentry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Reverse(application: Application) {
    private var repository: DataRepository
    val longValues = ConfigLongValues()
    var binaryString = String()

    init {
        repository = DataRepository.getInstance(
            RawMaterialsDatabase.getDatabase(application.baseContext)
        )
    }

    fun hexadecimalToBinaryString(hexadecimalValue: String): Boolean {
        hexadecimalValue.toCharArray().iterator().forEachRemaining {
            binaryString += hexToBinary(it.toString())
        }

        return binaryString.isNotEmpty()
    }

    fun hexToBinary(value: String): String {
        var result = String()
        when (value) {
            "0" -> {
                result = "0000"
            }
            "1" -> {
                result = "0001"
            }
            "2" -> {
                result = "0010"
            }
            "3" -> {
                result = "0011"
            }
            "4" -> {
                result = "0100"
            }
            "5" -> {
                result = "0101"
            }
            "6" -> {
                result = "0110"
            }
            "7" -> {
                result = "0111"
            }
            "8" -> {
                result = "1000"
            }
            "9" -> {
                result = "1001"
            }
            "A" -> {
                result = "1010"
            }
            "B" -> {
                result = "1011"
            }
            "C" -> {
                result = "1100"
            }
            "D" -> {
                result = "1101"
            }
            "E" -> {
                result = "1110"
            }
            "F" -> {
                result = "1111"
            }

        }
        return result
    }

    fun getSupplier(): String {
        return binaryString.subSequence(
            binaryString.length - longValues.supplierLong, binaryString.length
        ).toString().toBigInteger(2).toString()
    }

    fun getPiece(): String {
        return binaryString.subSequence(
            binaryString.length - (longValues.supplierLong + longValues.pieceLong),
            binaryString.length - longValues.supplierLong
        ).toString().toBigInteger(2).toString()
    }

    fun getSubVersion(): String {
        return binaryString.subSequence(
            binaryString.length - (longValues.supplierLong + longValues.pieceLong + longValues.subVersionLong),
            binaryString.length - (longValues.supplierLong + longValues.pieceLong)
        ).toString().toBigInteger(2).toString()

    }

    fun getType(): String {
        return binaryString.subSequence(
            binaryString.length - (longValues.supplierLong + longValues.pieceLong + longValues.subVersionLong + longValues.typeLong),
            binaryString.length - (longValues.supplierLong + longValues.pieceLong + longValues.subVersionLong)
        ).toString().toBigInteger(2).toString()
    }

    fun getVersion(): String {
        return binaryString.subSequence(
            binaryString.length - (longValues.supplierLong + longValues.pieceLong + longValues.subVersionLong + longValues.typeLong + longValues.versionLong),
            binaryString.length - (longValues.supplierLong + longValues.pieceLong + longValues.subVersionLong + longValues.typeLong)
        ).toString().toBigInteger(2).toString()

    }

    suspend fun checkValidTag(epc: String): Boolean = withContext(Dispatchers.IO) {
        try {
            var hexValue = hexadecimalToBinaryString(epc)
            var supplier: Int = getSupplier().toInt()
            if (supplier.toString().isNullOrEmpty())
                supplier = 0

            var validSupplier = 0
            CoroutineScope(Dispatchers.Main).launch {
                validSupplier = repository!!.getProvider(supplier)
                Log.e("epc", "${epc.toString()}")
                Log.e("proveedor", "${supplier.toString()}")
                Log.e("valido proveedor", "${validSupplier.toString()}")
            }
            if (validSupplier >= 1) {
                Log.e("valido", "")
                true
            } else {
                Log.e("invalido", "")
                false
            }

        } catch (ex: Exception) {
            Sentry.captureMessage("${ex.message}")
            Log.e("error listas", "${ex.toString()}")
            false
        }
    }

    fun getProvider(epc: String): Int {
        try {
            var hexValue = hexadecimalToBinaryString(epc)
            var supplier: Int = getSupplier().toInt()
            if (supplier.toString().isNullOrEmpty())
                supplier = 0

            return supplier
        }
        catch (ex: Exception) {
            Sentry.captureMessage("${ex.message}")
            return 0
        }
    }

}