package com.checkpoint.rfid_raw_material

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.PermissionChecker
import androidx.core.os.bundleOf
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.checkpoint.rfid_raw_material.bluetooth.BluetoothHandler
import com.checkpoint.rfid_raw_material.databinding.ActivityMainBinding
import com.checkpoint.rfid_raw_material.handheld.kt.Device
import com.checkpoint.rfid_raw_material.handheld.kt.DeviceInstanceBARCODE
import com.checkpoint.rfid_raw_material.handheld.kt.DeviceInstanceRFID
import com.checkpoint.rfid_raw_material.handheld.kt.interfaces.*
import com.checkpoint.rfid_raw_material.preferences.LocalPreferences
import com.checkpoint.rfid_raw_material.source.DataRepository
import com.checkpoint.rfid_raw_material.source.RawMaterialsDatabase
import com.checkpoint.rfid_raw_material.source.db.Tags
import com.checkpoint.rfid_raw_material.utils.CustomBattery
import com.checkpoint.rfid_raw_material.utils.dialogs.DialogSelectPairDevices
import com.checkpoint.rfid_raw_material.utils.dialogs.interfaces.SelectDeviceDialogInterface
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.isDenied
import com.fondesa.kpermissions.isGranted
import com.fondesa.kpermissions.request.PermissionRequest
import com.zebra.rfid.api3.*

import io.sentry.Sentry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter


class MainActivity : ActivityBase(), PermissionRequest.Listener,
    DeviceConnectStatusInterface,
    Readers.RFIDReaderEventHandler,
    ResponseHandlerInterface,
    BatteryHandlerInterface,
    BarcodeHandHeldInterface,
    WritingTagInterface,
    SelectDeviceDialogInterface,
    LevelPowerListHandlerInterface{
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var device: Device? = null


    private var readNumber: Int = 0
    private var tagsDetected: Int = 0
    private var singleTag = String()
    private var writeEnable = false
    private var epc: String? = null




    private val _liveCode: MutableLiveData<String> = MutableLiveData()
    var liveCode: LiveData<String> = _liveCode

    private val _batteryLevel: MutableLiveData<Int> = MutableLiveData()
    var batteryLevel: LiveData<Int> = _batteryLevel

    private val _maxPowerList: MutableLiveData<IntArray> = MutableLiveData()
    var maxPowerList: LiveData<IntArray> = _maxPowerList




    private val _deviceConnected: MutableLiveData<Boolean> = MutableLiveData()
    var deviceConnected: LiveData<Boolean> = _deviceConnected

    private val _showErrorDeviceConnected: MutableLiveData<Boolean> = MutableLiveData()
    var showErrorDeviceConnected: LiveData<Boolean> = _showErrorDeviceConnected

    private val _showErrorNumberTagsDetected: MutableLiveData<Boolean> = MutableLiveData()
    var showErrorNumberTagsDetected: LiveData<Boolean> = _showErrorNumberTagsDetected

    private val _showDialogWritingTag: MutableLiveData<Boolean> = MutableLiveData()
    var showDialogWritingTag: LiveData<Boolean> = _showDialogWritingTag


    private val _showDialogWritingError: MutableLiveData<Boolean> = MutableLiveData()
    var showDialogWritingError: LiveData<Boolean> = _showDialogWritingError

    private val _showDialogWritingSuccess: MutableLiveData<Boolean> = MutableLiveData()
    var showDialogWritingSuccess: LiveData<Boolean> = _showDialogWritingSuccess

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)
        repository = DataRepository.getInstance(
            RawMaterialsDatabase.getDatabase(application.baseContext)
        )

        localSharedPreferences = LocalPreferences(application)
        btnHandHeldGun = binding.appBarMain.imgHandHeldGun

        btnCreateLog = binding.appBarMain.imgCreateLog
        batteryView = binding.appBarMain.batteryView
        lyCreateLog = binding.appBarMain.lyCreateLog

        batteryView!!.visibility = View.GONE
        btnHandHeldGun!!.visibility = View.GONE
        lyCreateLog!!.visibility = View.GONE

      btnHandHeldGun!!.setOnClickListener {

          val readNumber= getReadNumber()

          val bundle = bundleOf(
              "readNumber" to readNumber)

          val navController = findNavController(R.id.nav_host_fragment_content_main)
          navController.navigate(R.id.handHeldConfigFragment,bundle)

        }

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.optionsWriteFragment), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S || "S".equals(Build.VERSION.CODENAME)) {
            // Android 12 or Android 12 Beta
            requestPermissions12.addListener(this)
        }
        else
            requestPermissions11.addListener(this)



    }

    fun startRFIDReadInstance(writeEnable: Boolean, epc: String) {
        this.writeEnable = writeEnable
        this.epc = epc
        if (deviceInstanceRFID != null) {
            deviceInstanceRFID!!.clean()

        }

        val mp = localSharedPreferences!!.getMaxFromPreferences()
        val sess = localSharedPreferences!!.getSessionFromPreferences()
        deviceInstanceRFID = DeviceInstanceRFID(device!!.getReaderDevice(),mp,sess)

        deviceInstanceRFID!!.setBatteryHandlerInterface(this)
        deviceInstanceRFID!!.setHandlerInterfacResponse(this)
        deviceInstanceRFID!!.setHandlerWriteInterfacResponse(this)
        deviceInstanceRFID!!.setHandlerLevelTransmisioPowerInterfacResponse(this)

        deviceInstanceRFID!!.setRfidModeRead()
        deviceInstanceRFID!!.battery()
        deviceInstanceRFID!!.transmitPowerLevels()

    }

    fun startBarCodeReadInstance() {
        deviceInstanceBARCODE = DeviceInstanceBARCODE(device!!.getReaderDevice(), applicationContext)
        deviceInstanceBARCODE!!.setBarCodeHandHeldInterface(this)
    }

    fun stopReadedBarCode() {
        deviceInstanceBARCODE!!.interruptBarCodeSession()
    }

    fun startDeviceConnection() {

        deviceName.isEmpty().apply {
            if (this)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S || "S".equals(Build.VERSION.CODENAME))
                    requestPermissions12.send()
            else
                    requestPermissions11.send()

        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onPermissionsResult(result: List<PermissionStatus>) {
        var res: Int = 0

        result.iterator().forEachRemaining {
            if (it.isGranted()) {
                Log.d(it.permission.toString(), "aceptado")
                res++
            } else if (it.isDenied()) {
                Log.d(it.permission.toString(), "denegado")
            }
        }

        if (res > 0)
            searchDevices()
        else
            finish()
    }

    override fun isConnected(b: Boolean) {

        Log.e("DEVICE SELECTED:", "$deviceName")
        Log.e("DEVICE STATUS:", "$b")
        if (b) {
            _deviceConnected.postValue(b)
            localSharedPreferences!!.getSessionFromPreferences().apply {
                if (this.isEmpty()){
                    localSharedPreferences!!.saveSessionToPreferences("SESSION_1")
                }
            }
        } else {
            _showErrorDeviceConnected.postValue(true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        device!!.disconnect()

    }

    override fun handleTagdata(tagData: Array<TagData?>?) {
        readNumber = localSharedPreferences!!.getReadNumber()

        if(this.writeEnable){
            tagsDetected ++
            singleTag = tagData?.get(0)?.tagID.toString()

        }else{

                tagData!!.iterator().forEachRemaining {

                    Log.e("TAG DATA","${it!!.tagID.toString()}")
                    CoroutineScope(Dispatchers.IO).launch {
                        newTag(it!!.tagID.toString(), readNumber)
                    }
                }


        }

    }


    override fun handleTriggerPress(pressed: Boolean) {
        Log.e("handleTriggerPress", "${pressed}")

        val isPause = localSharedPreferences!!.getPauseStatus()
        if(!isPause){
        if (pressed) {
            tagsDetected=0
            deviceInstanceRFID!!.perform()

        } else {
            Log.e("TAG NUMBER DETECTED","${tagsDetected}")
            deviceInstanceRFID!!.stop()
            if (this.writeEnable) {
                if(tagsDetected > 1){
                    _showErrorNumberTagsDetected.postValue(true)

                }else{
                    _showErrorNumberTagsDetected.postValue(false)
                    _showDialogWritingTag.postValue(true)
                    var epc = this.epc!!
                    CoroutineScope(Dispatchers.IO).launch {
                        newTag(epc, readNumber)
                    }
                    deviceInstanceRFID!!.writeTagMode(epc, singleTag)

                }
            }

        }

        }
    }
     suspend fun newTag(epc: String, readNumb: Int): Tags = withContext(Dispatchers.IO) {
        val nowDate: OffsetDateTime = OffsetDateTime.now()
        val formatter: DateTimeFormatter = DateTimeFormatter.ISO_INSTANT

        repository!!.insertNewTag(
            Tags(
                0,
                readNumb,
                "0",
                "0",
                "0",
                "0",
                0,
                epc,
                formatter.format(nowDate)
            )
        )
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
        batteryView!!.setPercent(level)
        _batteryLevel.postValue(level)

    }


    override fun setDataBarCode(code: String) {
        _liveCode.value = code.filter { it in '0'..'9' }
    }

    override fun connected(status: Boolean) {
        Log.e("connected", "$status")
    }

    override fun writingTagStatus(status: Boolean) {
        Log.e("writingTagStatus", "${status}")
        _showDialogWritingTag.postValue(false)

        if(status){

            this.writeEnable = false
            _showDialogWritingSuccess.postValue(true)
/*
            val bundle = bundleOf(
                "readNumber" to readNumber
            )

            val navController = findNavController(R.id.nav_host_fragment_content_main)
            navController.navigate(R.id.writeTagFragment, bundle)
            _liveCode.value = ""*/

        }else{

            _showDialogWritingError.postValue(true)
        }


    }



    @SuppressLint("MissingPermission")
    private fun searchDevices() {

        try{
            Log.e("searchDevices()", ".....")
            bluetoothHandler = BluetoothHandler(this)
            val devices = bluetoothHandler!!.list()
            var devicesRFID = listOf<String>()

            if (devices != null) {
                if (devices.size > 0) {

                    for (device in devices) {
                        if (device.name.contains("RFD8500")) {
                            devicesRFID += device.name
                        }
                    }
                    if (devicesRFID.size > 1) {


                        dialogSelectPairDevices = DialogSelectPairDevices(devicesRFID, this)
                        dialogSelectPairDevices!!.show()
                    } else {

                        if (devicesRFID.isNotEmpty()) {

                            deviceName = devicesRFID[0]
                            createDeviceInstance(deviceName!!)

                        } else {
                            dialogErrorDeviceConnected!!.show()

                        }
                    }

                } else {
                    dialogErrorDeviceConnected!!.show()
                    // DIALOG TURN ON BLUETOOTH
                }

            }
        }catch (ex: Exception){
            Log.e("Exception search devices","${ex.message}")
            Sentry.captureMessage("${ex.message}")
            _deviceConnected.postValue(false)
            _showErrorDeviceConnected.postValue(true)

        }


    }

    fun resetBarCode() {
        _liveCode.value = ""
    }
    fun restartWritingFlags(){
        _showErrorNumberTagsDetected.postValue(false)
        _showDialogWritingError.postValue(false)
        _showDialogWritingSuccess.postValue(false)
    }

    private fun createDeviceInstance(deviceName: String) {

        device = Device(this, deviceName, this)
        device!!.connect()
    }

    override fun setDevice(device: String) {

        deviceName = device

    }

    override fun transmitPowerLevelValues(level: IntArray) {
        _maxPowerList.postValue(level)
    }

    fun getReadNumber():Int {
        return localSharedPreferences!!.getReadNumber()
    }


}