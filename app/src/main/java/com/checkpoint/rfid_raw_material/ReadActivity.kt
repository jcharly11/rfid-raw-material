package com.checkpoint.rfid_raw_material

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.checkpoint.rfid_raw_material.bluetooth.BluetoothHandler
import com.checkpoint.rfid_raw_material.databinding.ActivityMainBinding
import com.checkpoint.rfid_raw_material.databinding.ActivityReadBinding
import com.checkpoint.rfid_raw_material.handheld.kt.Device
import com.checkpoint.rfid_raw_material.handheld.kt.DeviceInstanceBARCODE
import com.checkpoint.rfid_raw_material.handheld.kt.DeviceInstanceRFID
import com.checkpoint.rfid_raw_material.handheld.kt.interfaces.BatteryHandlerInterface
import com.checkpoint.rfid_raw_material.handheld.kt.interfaces.DeviceConnectStatusInterface
import com.checkpoint.rfid_raw_material.handheld.kt.interfaces.ResponseHandlerInterface
import com.checkpoint.rfid_raw_material.preferences.LocalPreferences
import com.checkpoint.rfid_raw_material.source.DataRepository
import com.checkpoint.rfid_raw_material.source.RawMaterialsDatabase
import com.checkpoint.rfid_raw_material.source.db.Tags
import com.checkpoint.rfid_raw_material.ui.inventory.PagerFragment
import com.checkpoint.rfid_raw_material.utils.ReverseStandAlone
import com.checkpoint.rfid_raw_material.utils.dialogs.*
import com.google.android.material.navigation.NavigationView
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
    private var dialogLookingForDevice: DialogLookingForDevice? = null
    private var dialogErrorDeviceConnected: DialogErrorDeviceConnected? = null
    private lateinit var device: Device
    private var deviceReady = false
    private var bluetoothHandler: BluetoothHandler? = null
    private var deviceName =  String()
    val reverse = ReverseStandAlone()
    private lateinit var binding: ActivityReadBinding

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
        loadFragment(PagerFragment())

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
    override fun onDestroy() {
        super.onDestroy()
        device!!.disconnect()

    }
    fun startRFIDReadInstance() {


        var mp = localSharedPreferences!!.getMaxFromPreferences()
        val sess = localSharedPreferences!!.getSessionFromPreferences()
        val volumeHH= localSharedPreferences!!.getVolumeHH()

        deviceInstanceRFID = DeviceInstanceRFID(device!!.getReaderDevice(), mp, sess,volumeHH)
        deviceInstanceRFID!!.setBatteryHandlerInterface(this)
        deviceInstanceRFID!!.setHandlerInterfacResponse(this)
        deviceInstanceRFID!!.setRfidModeRead()
        deviceInstanceRFID!!.battery()

    }

    private fun loadFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()
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
                    if(version.isNullOrEmpty()) version=""
                    if(subVersion.isNullOrEmpty()) subVersion=""
                    if(type.isNullOrEmpty()) type=""
                    if(piece.isNullOrEmpty()) piece=""
                    if(idProvider==null) idProvider=0
                    Log.e("---X","$epc")
                    localSharedPreferences!!.getReadNumber()
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
        if (pressed) {
             deviceInstanceRFID!!.perform()

        }else{
            deviceInstanceRFID!!.stop()

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


    }

    override fun isConnected(b: Boolean) {
        if (b) {
            dialogLookingForDevice!!.dismiss()
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