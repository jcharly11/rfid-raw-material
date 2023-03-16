package com.checkpoint.rfid_raw_material.handheld.kt

import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.Log
import com.checkpoint.rfid_raw_material.handheld.BarcodeHandHeldInterface
import com.zebra.rfid.api3.InvalidUsageException
import com.zebra.rfid.api3.OperationFailureException
import com.zebra.rfid.api3.Readers
import com.zebra.scannercontrol.*
import kotlinx.coroutines.*


class HandHeldBarCodeReader(): ZebraReader8500(),
    IDcsSdkApiDelegate {

    private var sdkHandler: SDKHandler? = null
    var mScannerInfoList = ArrayList<DCSScannerInfo>()
    private val BARCODE_RECEIVED = 1
    var connectedScannerID = 0
    private lateinit var barcodeHandHeldInterface: BarcodeHandHeldInterface
    private var deviceConfig: DeviceConfig?= null


    override suspend fun instance(context: Context?, device: DeviceConfig?){
        sdkHandler = SDKHandler(context!!)
        sdkHandler!!.dcssdkSetDelegate(this)
        readers = Readers(context, device!!.type)
        deviceConfig= device
        GlobalScope.launch(Dispatchers.Main) {

            val connected = async {
                connect()
            }
            connected.await().let {

                sdkHandler!!.dcssdkEstablishCommunicationSession(mScannerInfoList[0].scannerID)

            }
        }

    }

    fun setBarcodeResponseInterface(_barcodeHandHeldInterface: BarcodeHandHeldInterface){
        barcodeHandHeldInterface = _barcodeHandHeldInterface
    }

    private suspend fun connect(): Boolean{
        return withContext(Dispatchers.Default) {
            try {

                availableRFIDReaderList = readers.GetAvailableRFIDReaderList()
                if (availableRFIDReaderList.size != 0) {
                    if (availableRFIDReaderList.size == 1) {
                        readerDevice = availableRFIDReaderList[0]
                        reader = readerDevice.rfidReader

                    } else {
                        availableRFIDReaderList.iterator().forEachRemaining {
                            if (it.name.equals(deviceConfig!!.device))
                                reader = it.rfidReader

                        }
                    }
                }
                reader.connect()
                reader.Config.setTriggerMode(deviceConfig!!.mode, false)

                sdkHandler!!.dcssdkSetOperationalMode(DCSSDKDefs.DCSSDK_MODE.DCSSDK_OPMODE_BT_NORMAL)

                var notifications_mask = 0

                notifications_mask =
                    notifications_mask or (
                            DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SCANNER_APPEARANCE.value or
                                    DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SCANNER_DISAPPEARANCE.value)

                notifications_mask =  notifications_mask or (
                        DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SESSION_ESTABLISHMENT.value or
                                DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SESSION_TERMINATION.value)
                notifications_mask = notifications_mask or
                        DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_BARCODE.value

                sdkHandler!!.dcssdkSubsribeForEvents(notifications_mask)
                sdkHandler!!.dcssdkEnableAvailableScannersDetection(true)


                return@withContext  true

            } catch (ex: Exception) {
                return@withContext false
            }
        }
    }
    override  suspend fun disconnect() {
        GlobalScope.launch(Dispatchers.Main) {
            val disconnect = async {
                disconnectDevice()
            }
            disconnect.await()
            Log.e("DEVICE DISCONNECTED", "disconnecting " + reader.hostName)

        }

    }
    private suspend fun disconnectDevice(){
        return withContext(Dispatchers.Default) {
            try {
                if (reader != null) {
                    reader!!.disconnect()
                }
            } catch (e: InvalidUsageException) {
                e.printStackTrace()
            } catch (e: OperationFailureException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }
    private suspend fun dispose(){
        return withContext(Dispatchers.Default) {


        }
    }


    override suspend fun resume() {
    }
    override suspend fun perform() {

    }
    override suspend fun stop() {

    }




    override fun dcssdkEventScannerAppeared(p0: DCSScannerInfo?) {
        mScannerInfoList.add(p0!!)
        if (mScannerInfoList.size > 0) {
            val reader = mScannerInfoList[0]

        }
    }



    override fun dcssdkEventScannerDisappeared(p0: Int) {
        Log.e("SessionDisappeared(","${p0!!}")
    }

    override fun dcssdkEventCommunicationSessionEstablished(p0: DCSScannerInfo?) {
        connectedScannerID = p0!!.scannerID
    }

    override fun dcssdkEventCommunicationSessionTerminated(p0: Int) {
        Log.e("SessionTerminated(","${p0!!}")
    }

    override fun dcssdkEventBarcode(p0: ByteArray?, p1: Int, p2: Int) {
        val code: String = String(p0!!)
        dataHandler.obtainMessage(BARCODE_RECEIVED, code).sendToTarget()

    }

    override fun dcssdkEventImage(p0: ByteArray?, p1: Int) {
        Log.e("EventImage(","${p0!!.size}")
    }

    override fun dcssdkEventVideo(p0: ByteArray?, p1: Int) {
        Log.e("EventVideo(","${p0!!.size}")
    }

    override fun dcssdkEventBinaryData(p0: ByteArray?, p1: Int) {
        Log.e("EventBinaryData(","${p0!!.size}")
    }

    override fun dcssdkEventFirmwareUpdate(p0: FirmwareUpdateEvent?) {
        Log.e("FirmwareUpdate(","${p0!!.status.value}")
    }

    override fun dcssdkEventAuxScannerAppeared(p0: DCSScannerInfo?, p1: DCSScannerInfo?) {
        Log.e("AuxScannerAppeared","${p0!!.scannerID}")
    }


    private val dataHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                BARCODE_RECEIVED -> {
                    val code = msg.obj as String
                    Log.e("BARCODE_RECEIVED: ","$code")
                    barcodeHandHeldInterface.setDataBarCode(code)

                }
            }
        }
    }

}