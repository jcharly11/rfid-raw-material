package com.checkpoint.rfid_raw_material.handheld.kt

import android.content.Context
import android.util.Log
import com.checkpoint.rfid_raw_material.handheld.kt.interfaces.DeviceConnectStatusInterface
import com.zebra.rfid.api3.*
import kotlinx.coroutines.*

class Device(private val context: Context,
             private val deviceName: String,
             private  val deviceConnectStatusInterface: DeviceConnectStatusInterface) {

    private var availableRFIDReaderList: ArrayList<ReaderDevice>? = null
    private var readerDevice: ReaderDevice? = null
    private var readers = Readers(context, ENUM_TRANSPORT.BLUETOOTH)
    private var reader: RFIDReader?= null


    fun connect(){
        GlobalScope.launch(Dispatchers.Main) {

            val connected = async {
                connectionTask()
            }
            connected.await().let {

                if(reader != null){

                    Log.e("DEVICE CONNECTED", "connect " + reader!!.hostName+"$it")
                    deviceConnectStatusInterface.isConnected(it)

                } else{
                    deviceConnectStatusInterface.isConnected(false)
                }
            }

        }
    }

    fun getReaderDevice(): RFIDReader{
        return reader!!
    }
    fun disconnect(){
        GlobalScope.launch(Dispatchers.Main) {
            val disconnect = async {
                disConnectionTask()
            }
            disconnect.await().let {


            }
        }

    }
    private suspend fun connectionTask(): Boolean{
        return withContext(Dispatchers.Default) {
            try {
                availableRFIDReaderList = readers.GetAvailableRFIDReaderList()
                if (availableRFIDReaderList!!.size != 0) {
                    if (availableRFIDReaderList!!.size == 1) {
                        readerDevice = availableRFIDReaderList!![0]
                        reader = readerDevice!!.rfidReader

                    } else {
                        availableRFIDReaderList!!.iterator().forEachRemaining {
                            if (it.name.equals(deviceName))
                                reader = it.rfidReader
                        }
                    }
                }

                Log.e("device status:", "${reader!!.isConnected}")

                reader!!.connect()

                return@withContext  reader!!.isConnected

            } catch (e: InvalidUsageException) {
                e.printStackTrace()
                return@withContext false
            } catch (e: OperationFailureException) {
                e.printStackTrace()
                return@withContext false
            } catch (ex: NullPointerException){
                return@withContext false

            }

        }
    }
    private suspend fun disConnectionTask(): Boolean{
        return withContext(Dispatchers.Default) {
            try {

                if (reader != null) {
                    reader!!.disconnect()
                }
                return@withContext  reader!!.isConnected

            } catch (e: InvalidUsageException) {
                e.printStackTrace()
                return@withContext false
            } catch (e: OperationFailureException) {
                e.printStackTrace()
                return@withContext false
            }

        }
    }
}