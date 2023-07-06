package com.checkpoint.rfid_raw_material

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.checkpoint.rfid_raw_material.bluetooth.BluetoothHandler
import com.checkpoint.rfid_raw_material.databinding.ActivityReadBinding
import com.checkpoint.rfid_raw_material.handheld.kt.Device
import com.checkpoint.rfid_raw_material.handheld.kt.DeviceInstanceRFID
import com.checkpoint.rfid_raw_material.handheld.kt.interfaces.BatteryHandlerInterface
import com.checkpoint.rfid_raw_material.handheld.kt.interfaces.DeviceConnectStatusInterface
import com.checkpoint.rfid_raw_material.handheld.kt.interfaces.ResponseHandlerInterface
import com.checkpoint.rfid_raw_material.preferences.LocalPreferences
import com.checkpoint.rfid_raw_material.source.DataRepository
import com.checkpoint.rfid_raw_material.source.RawMaterialsDatabase
import com.checkpoint.rfid_raw_material.source.db.Tags
import com.checkpoint.rfid_raw_material.ui.handheld.HandHeldConfigFragment
import com.checkpoint.rfid_raw_material.ui.inventory.PagerFragment
import com.checkpoint.rfid_raw_material.utils.ReverseStandAlone
import com.checkpoint.rfid_raw_material.utils.dialogs.*
import com.zebra.rfid.api3.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class ReadActivity : ActivityBase(),
    ResponseHandlerInterface,
    Readers.RFIDReaderEventHandler,
    BatteryHandlerInterface,
    DeviceConnectStatusInterface {
    private  var  deviceInstanceRFID: DeviceInstanceRFID? = null
    private var deviceReady = false
    private var bluetoothHandler: BluetoothHandler? = null
    private var deviceName =  String()
    val reverse = ReverseStandAlone()
    private lateinit var binding: ActivityReadBinding
    var repository: DataRepository? = null
    private var fragmentHandHeldConfig : HandHeldConfigFragment? = null
    private var batteryLevel = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bluetoothHandler = BluetoothHandler(this)
        localSharedPreferences = LocalPreferences(application)
        repository = DataRepository.getInstance(
            RawMaterialsDatabase.getDatabase(application.baseContext)
        )
        dialogErrorDeviceConnected = DialogErrorDeviceConnected(this)
        dialogLookingForDevice= DialogLookingForDevice(this)
        fragmentHandHeldConfig = HandHeldConfigFragment()


        loadFragment(PagerFragment())

    }

    fun handHeldConfig(){
        deviceInstanceRFID!!.battery()
        val readNumber= localSharedPreferences!!.getReadNumber()

        fragmentHandHeldConfig!!.arguments = bundleOf(
            "readNumber" to readNumber,
            "batteryLevel" to batteryLevel,
            "activity" to "ReadActivity")


        loadFragment(fragmentHandHeldConfig!!)
    }

    fun removeFargment(){
        removeFragment(fragmentHandHeldConfig!!)
    }

    @SuppressLint("MissingPermission")
    override fun onStart() {
        super.onStart()
        bluetoothHandler!!.list()!!.forEach {
            if (it.name.contains("RFD8500")) {
                deviceName += it.name
            }
        }
        device = Device(applicationContext,deviceName!!,this)


        dialogLookingForDevice!!.show()
        device!!.connect()

    }


    override fun onStop() {
        super.onStop()
        device!!.disconnect()
    }

    fun startRFIDReadInstance() {


        var mp = localSharedPreferences!!.getMaxFromPreferences()
        val sess = localSharedPreferences!!.getSessionFromPreferences()
        val volumeHH= localSharedPreferences!!.getVolumeHH()

        deviceInstanceRFID = DeviceInstanceRFID(device!!.getReaderDevice(), mp, sess,volumeHH)

        deviceInstanceRFID!!.setBatteryHandlerInterface(this)
        deviceInstanceRFID!!.setHandlerInterfacResponse(this)
        deviceInstanceRFID!!.setRfidModeReadInventory()
        deviceInstanceRFID!!.battery()

    }


    override fun handleTagdata(tagData: Array<TagData?>?) {
        val isPause = localSharedPreferences!!.getPauseStatus()
        if(!isPause){
            tagData!!.iterator().forEachRemaining {

                CoroutineScope(Dispatchers.IO).launch {
                    var epc = it!!.tagID.toString()
                    reverse.hexadecimalToBinaryString(epc)
                    var version= reverse.getVersion()
                    var subVersion= reverse.getSubVersion()
                    var type= reverse.getType()
                    var piece= reverse.getPiece()
                    var idProvider= reverse.getProvider(epc)
                     newTag(epc,  localSharedPreferences!!.getReadNumber(), version , subVersion, type, piece, idProvider)                    }
            }
        }else{
            Log.e("---X","PAUSED")
        }
    }
    suspend fun newTag(epc: String, readNumb: Int,version:String, subVersion:String, type:String,
                       piece:String, provider:Int): Tags = withContext(Dispatchers.IO) {
        val nowDate: OffsetDateTime = OffsetDateTime.now()
        val formatter: DateTimeFormatter = DateTimeFormatter.ISO_INSTANT

        repository!!.insertNewTag(
            Tags(
                0,
                readNumb,
                version,
                subVersion,
                type,
                piece,
                provider,
                epc,
                formatter.format(nowDate)
            )
        )
    }
    override fun handleTriggerPress(pressed: Boolean) {

        val isPause = localSharedPreferences!!.getPauseStatus()

        if(!isPause){

            if (pressed) {
                deviceInstanceRFID!!.perform()

            }else{
                deviceInstanceRFID!!.stop()

            }

        }


    }
    override fun handleStartConnect(connected: Boolean) {
    }

    override fun RFIDReaderAppeared(p0: ReaderDevice?) {
        Log.e("RFIDReaderAppeared", "${p0!!.rfidReader.hostName}")
    }

    override fun RFIDReaderDisappeared(p0: ReaderDevice?) {
        Log.e("RFIDReaderDisappeared", "${p0!!.rfidReader.hostName}")
    }

    override fun batteryLevel(level: Int) {
        Log.e("BatteryLevel current", "${level}")
        batteryLevel = level
        batteryView!!.setPercent(level)


    }

    override fun isConnected(b: Boolean) {
        dialogLookingForDevice!!.dismiss()

        if (b) {
             localSharedPreferences!!.getSessionFromPreferences().apply {
                if (this.isEmpty()){
                    localSharedPreferences!!.saveSessionToPreferences("SESSION_0")

                }

            }
            startRFIDReadInstance()

        } else {
           dialogErrorDeviceConnected!!.show()
        }
    }
}