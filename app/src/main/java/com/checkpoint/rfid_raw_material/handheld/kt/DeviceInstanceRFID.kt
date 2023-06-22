package com.checkpoint.rfid_raw_material.handheld.kt


import android.util.Log
import com.checkpoint.rfid_raw_material.handheld.kt.interfaces.*
import com.zebra.rfid.api3.*
import io.sentry.Sentry
import kotlinx.coroutines.*


class  DeviceInstanceRFID(private val reader: RFIDReader,private val maxPower: Int,
                           private  val session_region: String,
                           private val volumeHH:Boolean){


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
             if(volumeHH)
                 reader.Config.beeperVolume = BEEPER_VOLUME.HIGH_BEEP
             else
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

    fun writeTagMode(tid: String,epc: String) {

        Sentry.captureMessage("Starting write mode")
        reader.Actions.Inventory.stop()
        Sentry.captureMessage("Inventory stoped")
        reader!!.Config.dpoState = DYNAMIC_POWER_OPTIMIZATION.DISABLE
        Sentry.captureMessage("Mode write setup")
        reader.Config.setAccessOperationWaitTimeout(2000)
        writeWait(tid,epc,"0")

    }


    fun writeWait(tid: String, epc: String, password: String){

        val data = "4000$epc"
        Log.e("tid ", "$tid")
        Log.e("epc", "$data")
        Log.e("password", "$password")
        try {
            val tagData: TagData = TagData()
            val tagAccess = TagAccess()
            val writeAccessParams = tagAccess.WriteAccessParams()
            writeAccessParams.accessPassword = password.toLong(16)
            writeAccessParams.memoryBank = MEMORY_BANK.MEMORY_BANK_EPC
            writeAccessParams.offset = 1
            writeAccessParams.setWriteData(data)
            writeAccessParams.writeRetries = 1
            writeAccessParams.writeDataLength = data.length / 4

            Sentry.captureMessage("Trying to write ${tagData.tagID}")
            reader!!.Actions.TagAccess.writeWait(
                tid,
                writeAccessParams,
                null,
                tagData,
                true,
                true
            ).apply {

                Sentry.captureMessage("Finish writing ($tid == ${tagData.tagID})")
            }
            Log.e("RESULT: ", tagData.tagID)
            writingTagInterface!!.writingTagStatus(true)


        } catch (e: InvalidUsageException) {

            Log.e("InvalidUsageException: ", e.info)
            Sentry.captureMessage(  "InvalidUsageException : ${e.info} | ${e.vendorMessage} }")
            writingTagInterface!!.writingTagStatus(false)

        } catch (e: OperationFailureException) {

            Log.e("OperationFailureException: ", e.vendorMessage)
            Sentry.captureMessage(  "OperationFailureException : ${e.results} | ${e.statusDescription} | ${e.vendorMessage}")
            writingTagInterface!!.writingTagStatus(false)

        }

    }




}