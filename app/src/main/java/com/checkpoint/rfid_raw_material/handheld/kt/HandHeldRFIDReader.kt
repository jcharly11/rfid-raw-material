@file:OptIn(DelicateCoroutinesApi::class)

package com.checkpoint.rfid_raw_material.handheld.kt

 import android.content.Context
 import android.util.Log
 import com.checkpoint.rfid_raw_material.handheld.kt.interfaces.BatteryHandlerInterface
 import com.checkpoint.rfid_raw_material.handheld.kt.interfaces.ResponseHandlerInterface
 import com.checkpoint.rfid_raw_material.handheld.kt.interfaces.WritingTagInterface
 import com.checkpoint.rfid_raw_material.handheld.kt.model.DeviceConfig

 import com.zebra.rfid.api3.*
 import kotlinx.coroutines.*

class RFIDHandler(val context: Context, private val config: DeviceConfig)  : Readers.RFIDReaderEventHandler {

    private var readers = Readers(context, ENUM_TRANSPORT.BLUETOOTH)
    private var availableRFIDReaderList: ArrayList<ReaderDevice>? = null
    private var readerDevice: ReaderDevice? = null
    private var reader: RFIDReader? = null
    private var eventHandler: EventHandler? = null
    private val triggerInfo = TriggerInfo()
    private var responseHandlerInterface: ResponseHandlerInterface? = null
    private var batteryHandlerInterface: BatteryHandlerInterface? = null
    private var writingTagInterface: WritingTagInterface? = null


    init{
        GlobalScope.launch(Dispatchers.Main) {

            Connect()
        }
    }

    fun Connect(){
        GlobalScope.launch(Dispatchers.Main) {

            val connected = async {
                connectionTask()
            }
            connected.await().let {

                Log.e("DEVICE CONNECTED", "connect " + reader!!.hostName+"$it")
                if (it){
                    reader!!.Actions.Inventory.stop()
                    reader!!.Config.dpoState = DYNAMIC_POWER_OPTIMIZATION.ENABLE

                }
                responseHandlerInterface!!.handleStartConnect(it)
            }

        }
    }
      @JvmName("setResponseHandlerInterface1")
      fun setResponseHandlerInterface(_responseHandlerInterface: ResponseHandlerInterface?) {
        responseHandlerInterface = _responseHandlerInterface
    }


     fun setBatteryHandlerInterface(_batteryHandlerInterface: BatteryHandlerInterface?) {
        batteryHandlerInterface = _batteryHandlerInterface
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
                           if (it.name.equals(config.device))
                               reader = it.rfidReader
                       }
                   }
               }

               Log.e("device status:", "${reader!!.isConnected}")

               reader!!.connect()
               triggerInfo.StartTrigger.triggerType =
                   START_TRIGGER_TYPE.START_TRIGGER_TYPE_IMMEDIATE
               triggerInfo.StopTrigger.triggerType = STOP_TRIGGER_TYPE.STOP_TRIGGER_TYPE_IMMEDIATE
               eventHandler = EventHandler()
               reader!!.Events.addEventsListener(eventHandler)
               reader!!.Events.setBatteryEvent(true)
               reader!!.Events.setHandheldEvent(true)
               reader!!.Events.setTagReadEvent(true)
               reader!!.Events.setAttachTagDataWithReadEvent(false)
               reader!!.Config.setTriggerMode(config.mode, false)
               reader!!.Config.startTrigger = triggerInfo.StartTrigger
               reader!!.Config.stopTrigger = triggerInfo.StopTrigger
               val antennaConfig = reader!!.Config.Antennas.getAntennaRfConfig(1)
               antennaConfig.transmitPowerIndex = config.maxPower
               antennaConfig.setrfModeTableIndex(0)
               antennaConfig.tari = 0
               reader!!.Config.Antennas.setAntennaRfConfig(1, antennaConfig)

               val singulationControl = reader!!.Config.Antennas.getSingulationControl(1)
               singulationControl.session = config.session

               singulationControl.Action.inventoryState = INVENTORY_STATE.INVENTORY_STATE_A
               singulationControl.Action.slFlag = SL_FLAG.SL_ALL
               reader!!.Config.Antennas.setSingulationControl(1, singulationControl)
               reader!!.Actions.PreFilters.deleteAll()
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




    private fun isReaderConnected(): Boolean {
        return if (reader!!.isConnected) true else {
            Log.d("isReaderConnected", "reader is not connected")
            false
        }
    }

    suspend fun prepareReaderToWrite():Boolean= withContext(Dispatchers.IO) {
         try {

             reader!!.Config.setAccessOperationWaitTimeout(1000)
             reader!!.Actions.Inventory.stop()
             reader!!.Config.dpoState = DYNAMIC_POWER_OPTIMIZATION.DISABLE

             true

        } catch (e: InvalidUsageException) {
            e.printStackTrace()
             false
        } catch (e: OperationFailureException) {
            e.printStackTrace()
             false
        }
    }

   suspend fun prepareReaderToRead(){
        try {

            reader!!.Actions.Inventory.stop()
            reader!!.Config.dpoState = DYNAMIC_POWER_OPTIMIZATION.ENABLE


        } catch (e: InvalidUsageException) {
            e.printStackTrace()
        } catch (e: OperationFailureException) {
            e.printStackTrace()
        }
    }


    fun write( tid: String, epc: String,  password: String){
        writeTag(tid, password, MEMORY_BANK.MEMORY_BANK_EPC, epc, 2)
    }
    @Synchronized
    private fun writeTag(
        sourceEPC: String,
        Password: String,
        memory_bank: MEMORY_BANK,
        targetData: String,
        offset: Int
    ) {
        Log.e("sourceEPC", "$sourceEPC")
        Log.e("targetData", "$targetData")
        Log.e("password", "$Password")


        try {
            val tagData: TagData? = null
            val tagAccess = TagAccess()
            val writeAccessParams = tagAccess.WriteAccessParams()
            writeAccessParams.accessPassword = Password.toLong(16)
            writeAccessParams.memoryBank = memory_bank
            writeAccessParams.offset = offset
            writeAccessParams.setWriteData(targetData)
            writeAccessParams.writeRetries = 3
            writeAccessParams.writeDataLength = targetData.length / 4
            val useTIDfilter = memory_bank === MEMORY_BANK.MEMORY_BANK_EPC
            reader!!.Actions.TagAccess.writeWait(
                sourceEPC,
                writeAccessParams,
                null,
                tagData,
                true,
                useTIDfilter
            )
            writingTagInterface!!.writingTagStatus(true)

        } catch (e: InvalidUsageException) {
            e.printStackTrace()
            writingTagInterface!!.writingTagStatus(false)

        } catch (e: OperationFailureException) {
            e.printStackTrace()
            writingTagInterface!!.writingTagStatus(false)

            Log.e("EXCEPTION", e.vendorMessage.toString())
            Log.e("RESULTS", e.results.toString())
            Log.e("RESULTS", e.statusDescription.toString())
        }
    }

    suspend fun stop(){
        try {

            if(reader!=null){

                reader!!.Actions.Inventory.stop()

            }
        } catch (e: InvalidUsageException) {

            e.printStackTrace()
        } catch (e: OperationFailureException) {
            e.printStackTrace()
        }
    }
    suspend fun perform(){

        try {
            reader!!.Actions.Inventory.perform()
        } catch (e: InvalidUsageException) {
            e.printStackTrace()
        } catch (e: OperationFailureException) {
            e.printStackTrace()
        }
    }
    private suspend fun dispose(){}
    suspend fun disconnect(){
        try {
            if (reader != null) {
                reader!!.Events.removeEventsListener(eventHandler)
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


    override fun RFIDReaderAppeared(p0: ReaderDevice?) {

        GlobalScope.launch(Dispatchers.Main) {

            val connected = async {
                connectionTask()
            }
            connected.await().let {
                Log.e("Device connected","${p0!!.rfidReader.hostName}")
            }

        }
    }

    override fun RFIDReaderDisappeared(p0: ReaderDevice?) {
        if (p0!!.name == reader!!.hostName){
            GlobalScope.launch(Dispatchers.Main) {
                val disconnect = async { disconnect()
                }
                disconnect.await().let {
                    Log.e("Device connected","${p0!!.rfidReader.hostName}")
                }
            }

        }
     }

    fun setWriteTagHandlerInterface(_writingTagInterface: WritingTagInterface) {

        writingTagInterface = _writingTagInterface
    }


    inner class  EventHandler : RfidEventsListener {
        override fun eventReadNotify(p0: RfidReadEvents?) {

             val tags = reader!!.Actions.getReadTags(100)
            for(tag in tags){

                if (tag.opCode === ACCESS_OPERATION_CODE.ACCESS_OPERATION_READ &&
                    tag.opStatus === ACCESS_OPERATION_STATUS.ACCESS_SUCCESS
                ) {
                    if (tag.memoryBankData.isNotEmpty()) {
                        Log.d("", tag.memoryBankData
                        )
                    }
                }


                if (tag.isContainsLocationInfo) {
                    val dist: Short = tag.LocationInfo.getRelativeDistance()
                    Log.d("","$dist"
                    )
                }
            }

            responseHandlerInterface!!.handleTagdata(tags)


        }

        override fun eventStatusNotify(rfidStatusEvents: RfidStatusEvents?) {
            Log.d("Status Notification: " , rfidStatusEvents!!.StatusEventData.statusEventType.toString()

            )

            if (rfidStatusEvents.StatusEventData.statusEventType ===
                STATUS_EVENT_TYPE.HANDHELD_TRIGGER_EVENT) {

                if (rfidStatusEvents.StatusEventData.HandheldTriggerEventData.handheldEvent ===
                    HANDHELD_TRIGGER_EVENT_TYPE.HANDHELD_TRIGGER_PRESSED) {
                    responseHandlerInterface!!.handleTriggerPress(true)
                }
                if (rfidStatusEvents.StatusEventData.HandheldTriggerEventData.handheldEvent ===
                    HANDHELD_TRIGGER_EVENT_TYPE.HANDHELD_TRIGGER_RELEASED) {
                    responseHandlerInterface!!.handleTriggerPress(false)

                }

                }
            if (rfidStatusEvents.StatusEventData.statusEventType ===
                STATUS_EVENT_TYPE.BATTERY_EVENT) {
                val batteryData = rfidStatusEvents.StatusEventData.BatteryData
                Log.e("BatteryData", batteryData.level.toString())
                batteryHandlerInterface!!.batteryLevel(batteryData.level)
            }


            }

    }

    fun batteryLevel() {
        try {
            if (reader != null && reader!!.Config != null) {
               reader!!.Config.getDeviceStatus(true, true, false)
            }
        } catch (e: InvalidUsageException) {
            e.printStackTrace()
        } catch (e: OperationFailureException) {
            e.printStackTrace()
        }
    }
}