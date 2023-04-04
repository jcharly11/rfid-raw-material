package com.checkpoint.rfid_raw_material.handheld.kt

import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.Log
import com.checkpoint.rfid_raw_material.handheld.kt.interfaces.BarcodeHandHeldInterface
import com.zebra.rfid.api3.ENUM_TRIGGER_MODE
import com.zebra.rfid.api3.RFIDReader
import com.zebra.scannercontrol.*
import kotlinx.coroutines.*

class DeviceInstanceBARCODE(private val reader: RFIDReader,
                            private val context: Context): IDcsSdkApiDelegate {
    private var sdkHandler = SDKHandler(context)
    private var barcodeHandHeldInterface: BarcodeHandHeldInterface? = null
    var mScannerInfoList = ArrayList<DCSScannerInfo>()
    private val BARCODE_RECEIVED = 1
    var connectedScannerID = 0
    var delageteContext = this

    init{

        GlobalScope.launch {

            val connected = async {
                connectTask()
            }
            connected.await().let {
                if(mScannerInfoList.size>0) {

                    sdkHandler!!.dcssdkEstablishCommunicationSession(mScannerInfoList[0].scannerID)

                }


            }


        }

    }

    private suspend fun connectTask(): Boolean{
        return withContext(Dispatchers.Default) {
            try {

                reader.Config.setTriggerMode(ENUM_TRIGGER_MODE.BARCODE_MODE, false)
                sdkHandler.dcssdkSetDelegate(delageteContext)
                sdkHandler.dcssdkSetOperationalMode(DCSSDKDefs.DCSSDK_MODE.DCSSDK_OPMODE_BT_NORMAL)
                var notificationsMask = 0

                notificationsMask =
                    notificationsMask or (
                            DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SCANNER_APPEARANCE.value or
                                    DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SCANNER_DISAPPEARANCE.value)

                notificationsMask =  notificationsMask or (
                        DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SESSION_ESTABLISHMENT.value or
                                DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SESSION_TERMINATION.value)
                notificationsMask = notificationsMask or
                        DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_BARCODE.value
                sdkHandler.dcssdkEstablishCommunicationSession(connectedScannerID)
                sdkHandler.dcssdkSubsribeForEvents(notificationsMask)
                sdkHandler.dcssdkEnableAvailableScannersDetection(true)
                true

            }catch (ex: Exception){
                false
            }
        }
    }
    private suspend fun disConnectTask(): Boolean{
        return withContext(Dispatchers.Default) {
            try {
                sdkHandler.dcssdkTerminateCommunicationSession(mScannerInfoList[0].scannerID).let {
                   true
                }
            }catch (ex: Exception){
                false

            }            }
        }

    fun setBarCodeHandHeldInterface(barcodeHandHeldInterface: BarcodeHandHeldInterface){
        this.barcodeHandHeldInterface = barcodeHandHeldInterface
    }

    fun interruptBarCodeSession(){
        GlobalScope.launch {
            val disconnect = async {
                disConnectTask()
            }
            disconnect.await().let {

            }
        }
    }

    override fun dcssdkEventScannerAppeared(p0: DCSScannerInfo?) {
        Log.e("dcssdkEventScannerAppeared","${p0!!.scannerID}")
        mScannerInfoList.add(p0!!)

    }


    override fun dcssdkEventScannerDisappeared(p0: Int) {
        Log.e("SessionDisappeared(","${p0!!}")


    }

    override fun dcssdkEventCommunicationSessionEstablished(p0: DCSScannerInfo?) {
        Log.e("dcssdkEventCommunicationSessionEstablished(","${p0!!}")

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
        sdkHandler!!.dcssdkEstablishCommunicationSession(p0!!.scannerID)

    }


    private val dataHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                BARCODE_RECEIVED -> {
                    val code = msg.obj as String
                    Log.e("BARCODE_RECEIVED: ","$code")

                }
            }
        }
    }


}