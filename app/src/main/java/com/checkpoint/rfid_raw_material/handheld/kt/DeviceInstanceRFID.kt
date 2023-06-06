package com.checkpoint.rfid_raw_material.handheld.kt


import android.util.Log
import com.checkpoint.rfid_raw_material.handheld.kt.interfaces.*
import com.zebra.rfid.api3.*
import io.sentry.Sentry
import kotlinx.coroutines.*


class  DeviceInstanceRFID(private val reader: RFIDReader,private val maxPower: Int,
                           private  val session_region: String){


     private var eventHandler: EventHandler? = null
     private val triggerInfo = TriggerInfo()

     private var responseHandlerInterface: ResponseHandlerInterface? = null
     private var batteryHandlerInterface: BatteryHandlerInterface? = null
     private var writingTagInterface: WritingTagInterface? = null
     private var levelPowerListHandlerInterface: LevelPowerListHandlerInterface? = null
    private var unavailableDeviceInterface: UnavailableDeviceInterface? = null


    private suspend fun performTask(){
        return withContext(Dispatchers.Default) {
            try {
                reader!!.Actions.Inventory.perform()
            } catch (e: InvalidUsageException) {
                e.printStackTrace()
            } catch (e: OperationFailureException) {
                e.printStackTrace()
            }
        }
    }
    private suspend fun stopTask(){
        return withContext(Dispatchers.Default) {
            try {
                reader!!.Actions.Inventory.stop()
            } catch (e: InvalidUsageException) {
                e.printStackTrace()
            } catch (e: OperationFailureException) {
                e.printStackTrace()
            }
        }
    }
     private suspend fun cleanTask(){
         return withContext(Dispatchers.Default) {
             try {
                 reader.Events.removeEventsListener(eventHandler)
              } catch (e: InvalidUsageException) {
                 e.printStackTrace()
             } catch (e: OperationFailureException) {
                 e.printStackTrace()
             }
         }
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

             Log.d("eventReadNotify: " , "${tags.size}")
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


     fun setBatteryHandlerInterface(batteryHandlerInterface: BatteryHandlerInterface){
         this.batteryHandlerInterface = batteryHandlerInterface
     }
     fun setHandlerInterfacResponse(responseHandlerInterface: ResponseHandlerInterface){
         this.responseHandlerInterface = responseHandlerInterface

     }
     fun setHandlerWriteInterfacResponse(writingTagInterface: WritingTagInterface){
         this.writingTagInterface = writingTagInterface

     }
    fun setHanHeldUnavailableInterface(unavailableDeviceInterface: UnavailableDeviceInterface){
        this.unavailableDeviceInterface = unavailableDeviceInterface
    }


     fun setHandlerLevelTransmisioPowerInterfacResponse(levelPowerListHandlerInterface: LevelPowerListHandlerInterface){
         this.levelPowerListHandlerInterface=levelPowerListHandlerInterface
     }

     fun setRfidModeRead(){
         try{

             triggerInfo.StartTrigger.triggerType =
                 START_TRIGGER_TYPE.START_TRIGGER_TYPE_IMMEDIATE
             triggerInfo.StopTrigger.triggerType = STOP_TRIGGER_TYPE.STOP_TRIGGER_TYPE_IMMEDIATE
             if(eventHandler == null)
                 eventHandler = EventHandler()
             reader.Events.addEventsListener(eventHandler)
             reader.Events.setBatteryEvent(true)
             reader.Events.setHandheldEvent(true)
             reader.Events.setTagReadEvent(true)
             reader.Events.setAttachTagDataWithReadEvent(false)
             reader.Config.setTriggerMode(ENUM_TRIGGER_MODE.RFID_MODE, false)
             reader.Config.startTrigger = triggerInfo.StartTrigger
             reader.Config.stopTrigger = triggerInfo.StopTrigger
             reader.Config.beeperVolume = BEEPER_VOLUME.QUIET_BEEP
             val antennaConfig = reader.Config.Antennas.getAntennaRfConfig(1)
             antennaConfig.transmitPowerIndex = maxPower
             antennaConfig.setrfModeTableIndex(0)
             antennaConfig.tari = 0
             val tagStorageSettings = reader.Config.tagStorageSettings
             tagStorageSettings.setTagFields(TAG_FIELD.ALL_TAG_FIELDS)

             tagStorageSettings.isAccessReportsEnabled
             reader.Config.tagStorageSettings = tagStorageSettings

             reader.Config.Antennas.setAntennaRfConfig(1, antennaConfig)

             val singulationControl = reader.Config.Antennas.getSingulationControl(1)

             val session= when (session_region) {
                 "SESSION_1" -> {
                     SESSION.SESSION_S1
                 }
                 "SESSION_2" -> {
                     SESSION.SESSION_S2
                 }
                 else -> {
                     SESSION.SESSION_S3
                 }
             }

             singulationControl.session = session
             singulationControl.Action.inventoryState = INVENTORY_STATE.INVENTORY_STATE_A
             singulationControl.Action.slFlag = SL_FLAG.SL_ALL
             reader.Config.Antennas.setSingulationControl(1, singulationControl)
             reader.Actions.PreFilters.deleteAll()

         }catch (ex: Exception){
             unavailableDeviceInterface!!.deviceCharging()
         }
     }
     fun stop(){
         GlobalScope.launch(Dispatchers.Main) {
             val stop = async {
                 stopTask()
             }
         }

     }
     fun perform(){
         GlobalScope.launch(Dispatchers.Main) {
             val perform = async {
                 performTask()
             }
         }

     }
     fun clean(){
         GlobalScope.launch(Dispatchers.Main) {
             val clean = async {
                 cleanTask()
             }
         }

     }

     fun battery() {
             try {
                 if (reader.Config != null) {
                     reader.Config.getDeviceStatus(true, true, false)
                 }
             } catch (e: InvalidUsageException) {
                 e.printStackTrace()
             } catch (e: OperationFailureException) {
                 e.printStackTrace()
             }
     }

     fun transmitPowerLevels(){
            if(reader!= null){
                levelPowerListHandlerInterface!!.transmitPowerLevelValues(reader.ReaderCapabilities.transmitPowerLevelValues)

            }
     }

     fun writeTagMode(epc: String, tid: String) {
         try {
             Log.e("writeMode","started")
             reader.Config.setAccessOperationWaitTimeout(5000)
             reader.Actions.Inventory.stop()
             reader!!.Config.dpoState = DYNAMIC_POWER_OPTIMIZATION.DISABLE
             write(tid,epc,"0")

         } catch (e: InvalidUsageException) {
             e.printStackTrace()
         } catch (e: OperationFailureException) {
             e.printStackTrace()
         }
     }

     fun write( tid: String, epc: String,  password: String){

         Log.e("tid ", "$tid")
         Log.e("epc", "$epc")
         Log.e("password", "$password")
        // writeTAGID(tid, epc)
        //writeTag(tid, password, MEMORY_BANK.MEMORY_BANK_EPC, epc, 2)
         writeWait(tid,epc)

     }
     @Synchronized
     private fun writeTag(
         sourceEPC: String,
         Password: String,
         memory_bank: MEMORY_BANK,
         targetData: String,
         offset: Int
     ) {

         try {
             val tagData: TagData = TagData()
             val tagAccess = TagAccess()
             val length =  targetData.length / 4
             targetData.encodeToByteArray()
             val writeAccessParams = tagAccess.WriteAccessParams()
             writeAccessParams.accessPassword = Password.toLong(16)
             writeAccessParams.memoryBank = memory_bank
             writeAccessParams.offset = offset
             writeAccessParams.setWriteData(targetData)
             writeAccessParams.writeRetries = 3
             writeAccessParams.writeDataLength = length
             val useTIDfilter = memory_bank === MEMORY_BANK.MEMORY_BANK_EPC
             writeAccessParams.byteOffset
             reader!!.Actions.TagAccess.writeWait(
                 sourceEPC,
                 writeAccessParams,
                 null,
                 tagData,true,useTIDfilter
             )

             Log.e("Result tagID",tagData.tagID)
             Log.e("Result pc",tagData.pc.toString())


             writingTagInterface!!.writingTagStatus(true)

         } catch (e: InvalidUsageException) {
             e.printStackTrace()
             writingTagInterface!!.writingTagStatus(false)
            // Sentry.captureMessage("${e.message.toString()}")
             Log.e("InvalidUsageException", e.info)

         } catch (e: OperationFailureException) {
             e.printStackTrace()
             writingTagInterface!!.writingTagStatus(false)
             Log.e("OperationFailureException", e.message.toString())

             //Sentry.captureMessage("${e.message.toString()}")
             Log.e("EXCEPTION", e.vendorMessage.toString())
             Log.e("RESULTS", e.results.toString())
             Log.e("RESULTS", e.statusDescription.toString())
         }

     }

    @Synchronized
    private fun writeTag2(
        tid: String,
        Password: String,
        memory_bank: MEMORY_BANK,
        targetData: String,
        offset: Int
    ) {

        try {

            val writeSpecificFieldAccessParams = TagAccess().WriteSpecificFieldAccessParams()
            val data = targetData.encodeToByteArray()
            val length = data.size / 2
            Log.e("LENGTH DATA:","$length")
            writeSpecificFieldAccessParams.writeDataLength = 16
            writeSpecificFieldAccessParams.accessPassword = Password.toLong(16)
            writeSpecificFieldAccessParams.writeData = data
            reader!!.Actions.TagAccess.writeTagIDWait(tid,writeSpecificFieldAccessParams,null)


           // writingTagInterface!!.writingTagStatus(true)

        } catch (e: InvalidUsageException) {
            e.printStackTrace()
            writingTagInterface!!.writingTagStatus(false)
            Sentry.captureMessage("${e.message.toString()}")

        } catch (e: OperationFailureException) {
            e.printStackTrace()
           // writingTagInterface!!.writingTagStatus(false)

            Sentry.captureMessage("${e.message.toString()}")
            Log.e("EXCEPTION", e.vendorMessage.toString())
            Log.e("RESULTS", e.results.toString())
            Log.e("RESULTS", e.statusDescription.toString())
        }

    }


     fun writeTAGID(tid: String, epc: String){
         try {
             val data = epc.encodeToByteArray()
             val tagAccess = TagAccess()
             val writeAccessParams = tagAccess.WriteSpecificFieldAccessParams()
             writeAccessParams.accessPassword = 0
             writeAccessParams.writeDataLength = data.size
             writeAccessParams.writeData = data
              reader.Actions.TagAccess.writeTagIDWait(tid, writeAccessParams, null)

         } catch (e: InvalidUsageException) {
             e.printStackTrace()
             Log.e("EXCEPTION", e.vendorMessage.toString())
             Log.e("RESULTS", e.message.toString())

         } catch (e: OperationFailureException) {
             e.printStackTrace()

             Log.e("EXCEPTION", e.vendorMessage.toString())
             Log.e("RESULTS", e.results.toString())
             Log.e("RESULTS", e.statusDescription.toString())
         }

     }
    fun erase(tagId: String,epc: String){
        val tagAccess = TagAccess()
        val tagData = TagData()

        try {
            val eraseParams = tagAccess.BlockEraseAccessParams()
            eraseParams.accessPassword = 0
            eraseParams.memoryBank = MEMORY_BANK.MEMORY_BANK_EPC
            eraseParams.offset = 0
            eraseParams.count = 8
            reader.Actions.TagAccess.blockEraseWait(tagId, eraseParams, null, tagData)
            Log.e("blockEraseWait", tagData.tagID)
            // writeTagMode(epc,tagId)

        } catch (e: InvalidUsageException) {
            e.printStackTrace()
            Log.e("EXCEPTION", e.vendorMessage.toString())
            Log.e("RESULTS", e.message.toString())

        } catch (e: OperationFailureException) {
            e.printStackTrace()

            Log.e("EXCEPTION", e.vendorMessage.toString())
            Log.e("RESULTS", e.results.toString())
            Log.e("RESULTS", e.statusDescription.toString())
        }
    }
    fun readData(tid: String){
        var readAccess = TagAccess().ReadAccessParams()
        readAccess.accessPassword = 0L
        readAccess.memoryBank = MEMORY_BANK.MEMORY_BANK_USER
        readAccess.offset = 0
        var tagData = reader.Actions.TagAccess.readWait(tid,readAccess,null)


    }
    fun writeWithFilters(tag: String,writeData: String){

        val accessFilter = AccessFilter()
        val tagMask = tag.encodeToByteArray()
        accessFilter.TagPatternA.memoryBank = MEMORY_BANK.MEMORY_BANK_EPC
        accessFilter.TagPatternA.tagPattern = byteArrayOf(0x45, 0x32)
        accessFilter.TagPatternA.tagPatternBitCount = 2 * 8
        accessFilter.TagPatternA.bitOffset = 0
        accessFilter.TagPatternA.tagMask = tagMask
        accessFilter.TagPatternA.tagMaskBitCount = tagMask.size * 8
        accessFilter.accessFilterMatchPattern = FILTER_MATCH_PATTERN.A
        val tagAccess =  TagAccess()
        val writeAccessParams =  tagAccess.WriteAccessParams()
        writeAccessParams.accessPassword = 0
        writeAccessParams.memoryBank = MEMORY_BANK.MEMORY_BANK_USER
        writeAccessParams.offset = 0
        writeAccessParams.setWriteData(writeData)
        reader.Actions.TagAccess.writeEvent(writeAccessParams, accessFilter, null)

    }

    fun writeWait(tid: String, epc: String){
        val epcBytes= epc.toByteArray()
        val bytes = listOf(
            0x74,
            0xED, 0x40, 0x00, 0x33 ,0x31, 0x43, 0x43, 0x24, 0x14, 0x31, 0x41,0x41,0x24, 0x41, 0x34, 0x14, 0x41, 0x24, 0x17)


        val bt = bytes.map { it.toByte() }.toByteArray()

        Log.e("",epcBytes.contentToString())
        Log.e("","$bt")

        try {
            val accessParams = TagAccess().WriteAccessParams()
            accessParams.memoryBank = MEMORY_BANK.MEMORY_BANK_EPC
            accessParams.writeData = bt
            accessParams.writeDataLength = bytes.size / 4
            accessParams.offset = 0
            accessParams.accessPassword = 0L

            val dataResult = TagData()
            reader.Actions.TagAccess.writeWait(tid, accessParams, null, dataResult)

            Log.e("RESULT: ", dataResult.tagID)
        } catch (e: InvalidUsageException) {

            Log.e("InvalidUsageException: ", e.printStackTrace().toString())

        } catch (e: OperationFailureException) {

            Log.e("OperationFailureException: ", e.printStackTrace().toString())
        }

    }

}