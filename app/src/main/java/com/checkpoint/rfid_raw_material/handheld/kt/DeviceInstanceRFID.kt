package com.checkpoint.rfid_raw_material.handheld.kt


import android.util.Log
import com.checkpoint.rfid_raw_material.handheld.kt.interfaces.BatteryHandlerInterface
import com.checkpoint.rfid_raw_material.handheld.kt.interfaces.LevelPowerListHandlerInterface
import com.checkpoint.rfid_raw_material.handheld.kt.interfaces.ResponseHandlerInterface
import com.checkpoint.rfid_raw_material.handheld.kt.interfaces.WritingTagInterface
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


     fun setHandlerLevelTransmisioPowerInterfacResponse(levelPowerListHandlerInterface: LevelPowerListHandlerInterface){
         this.levelPowerListHandlerInterface=levelPowerListHandlerInterface
     }

     fun setRfidModeRead(){
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
         val antennaConfig = reader.Config.Antennas.getAntennaRfConfig(1)
         antennaConfig.transmitPowerIndex = maxPower
         antennaConfig.setrfModeTableIndex(0)
         antennaConfig.tari = 0
         val tagStorageSettings = reader.Config.tagStorageSettings
         tagStorageSettings.setTagFields(TAG_FIELD.ALL_TAG_FIELDS)
         tagStorageSettings.isAccessReportsEnabled
        // tagStorageSettings.maxTagIDLength = 8
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
         levelPowerListHandlerInterface!!.transmitPowerLevelValues(reader.ReaderCapabilities.transmitPowerLevelValues)
     }
     fun writeTagMode(epc: String, tid: String) {
         try {
             Log.e("writeMode","started")
             reader.Config.setAccessOperationWaitTimeout(1000)
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

         try {
             val tagData: TagData = TagData()
             val tagAccess = TagAccess()
             val length =  targetData.length / 4
             val writeAccessParams = tagAccess.WriteAccessParams()
             writeAccessParams.accessPassword = Password.toLong(16)
             writeAccessParams.memoryBank = memory_bank
             writeAccessParams.offset = offset
             writeAccessParams.setWriteData(targetData)
             writeAccessParams.writeRetries = 3
             writeAccessParams.writeDataLength = length
             Log.e("writeDataLength","${length}")


             val useTIDfilter = memory_bank === MEMORY_BANK.MEMORY_BANK_EPC
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
            writeSpecificFieldAccessParams.writeDataLength = length
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

    fun readData(tid: String){
        var readAccess = TagAccess().ReadAccessParams()
        readAccess.accessPassword = 0L
        readAccess.memoryBank = MEMORY_BANK.MEMORY_BANK_USER
        readAccess.offset = 0
        var tagData = reader.Actions.TagAccess.readWait(tid,readAccess,null)
        Log.e("---->",tagData.tagID)
        Log.e("---->",tagData.pc.toString())
        if (tagData.opCode == ACCESS_OPERATION_CODE.ACCESS_OPERATION_READ &&
            tagData.opStatus == ACCESS_OPERATION_STATUS.ACCESS_SUCCESS) {
            if (tagData.memoryBankData.isNotEmpty()) {
                Log.e("TAG DATA", " Mem Bank Data " + tagData.memoryBankData);
            }
        }

    }
    private suspend fun readTask(tid: String){
        return withContext(Dispatchers.Default) {
            try {

            } catch (e: InvalidUsageException) {
                e.printStackTrace()
            } catch (e: OperationFailureException) {
                e.printStackTrace()
            }
        }
    }


}