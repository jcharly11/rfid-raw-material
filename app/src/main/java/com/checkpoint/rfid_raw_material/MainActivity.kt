package com.checkpoint.rfid_raw_material

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.NavHostFragment
import com.checkpoint.rfid_raw_material.bluetooth.BluetoothHandler
import com.checkpoint.rfid_raw_material.databinding.ActivityMainBinding
import com.checkpoint.rfid_raw_material.enums.TypeLoading
import com.checkpoint.rfid_raw_material.handheld.kt.Device
import com.checkpoint.rfid_raw_material.handheld.kt.DeviceInstanceBARCODE
import com.checkpoint.rfid_raw_material.handheld.kt.DeviceInstanceRFID
import com.checkpoint.rfid_raw_material.handheld.kt.interfaces.*
import com.checkpoint.rfid_raw_material.preferences.LocalPreferences
import com.checkpoint.rfid_raw_material.source.DataRepository
import com.checkpoint.rfid_raw_material.source.RawMaterialsDatabase
import com.checkpoint.rfid_raw_material.source.db.Tags
import com.checkpoint.rfid_raw_material.utils.CustomBattery
import com.checkpoint.rfid_raw_material.utils.dialogs.DialogLookingForDevice
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.isDenied
import com.fondesa.kpermissions.isGranted
import com.fondesa.kpermissions.request.PermissionRequest
import com.zebra.rfid.api3.ReaderDevice
import com.zebra.rfid.api3.Readers
import com.zebra.rfid.api3.TagData
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
    BarcodeHandHeldInterface ,
    WritingTagInterface{
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    var btnHandHeldGun: AppCompatImageView? = null
    var batteryView: CustomBattery? = null
    var btnCreateLog: AppCompatImageView? = null
    var lyCreateLog: LinearLayout? = null
    lateinit var device: Device
    var deviceName: String = String()


     private var readNumber: Int = 0
    private var  writeEnable = false
    private var  epc: String?= null

    private val requestPermissions by lazy {
        permissionsBuilder(Manifest.permission.BLUETOOTH_CONNECT,Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_SCAN,Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION).build()
    }

    private val _liveCode: MutableLiveData<String> = MutableLiveData()
    var liveCode: LiveData<String> = _liveCode

    private val _deviceConnected: MutableLiveData<Boolean> = MutableLiveData()
    var deviceConnected: LiveData<Boolean> = _deviceConnected

    private val _showErrorDeviceConnected: MutableLiveData<Boolean> = MutableLiveData()
    var showErrorDeviceConnected: LiveData<Boolean> = _showErrorDeviceConnected

    private val _showErrorNumberTagsDetected: MutableLiveData<Boolean> = MutableLiveData()
    var showErrorNumberTagsDetected: LiveData<Boolean> = _showErrorNumberTagsDetected

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)
         repository = DataRepository.getInstance(
             RawMaterialsDatabase.getDatabase(application.baseContext)
         )

        localSharedPreferences = LocalPreferences(application)
        btnHandHeldGun= binding.appBarMain.imgHandHeldGun
        btnCreateLog= binding.appBarMain.imgCreateLog
        batteryView = binding.appBarMain.batteryView
        lyCreateLog = binding.appBarMain.lyCreateLog

        batteryView!!.visibility = View.GONE
        btnHandHeldGun!!.visibility = View.GONE
        lyCreateLog!!.visibility = View.GONE


         val drawerLayout: DrawerLayout = binding.drawerLayout
         val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
         val navController = navHostFragment.navController

         appBarConfiguration = AppBarConfiguration(
             setOf(R.id.optionsWriteFragment), drawerLayout
         )
         setupActionBarWithNavController(navController, appBarConfiguration)
         requestPermissions.addListener(this)





     }

    fun startRFIDReadInstance(writeEnable: Boolean,epc: String){
        this.writeEnable = writeEnable
        this.epc = epc
        if(deviceInstanceRFID != null){
            deviceInstanceRFID!!.clean()

        }
        deviceInstanceRFID =  DeviceInstanceRFID(device.getReaderDevice())
        deviceInstanceRFID!!.setBatteryHandlerInterface(this)
        deviceInstanceRFID!!.setHandlerInterfacResponse(this)
        deviceInstanceRFID!!.setHandlerWriteInterfacResponse(this)
        deviceInstanceRFID!!.setRfidModeRead()
        deviceInstanceRFID!!.battery()

    }
    fun startBarCodeReadInstance(){
        deviceInstanceBARCODE= DeviceInstanceBARCODE(device.getReaderDevice(),applicationContext)
        deviceInstanceBARCODE!!.setBarCodeHandHeldInterface(this)
    }
    fun stopReadedBarCode(){
        deviceInstanceBARCODE!!.interruptBarCodeSession()
    }
    fun startDeviceConnection(){

        deviceName.isEmpty().apply {
            if(this)
                requestPermissions.send()

        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onPermissionsResult(result: List<PermissionStatus>) {
        var res:Int= 0

        result.iterator().forEachRemaining{
            if(it.isGranted()==true) {
                Log.d(it.permission.toString(),"aceptado")
                res++
            }
            else if(it.isDenied()) {
                Log.d(it.permission.toString(),"denegado")
            }
        }

        if(res>0)
            searchDevices()
        else
            finish()
    }

    override fun isConnected(b: Boolean) {

        Log.e("DEVICE SELECTED:", "$deviceName")
        Log.e("DEVICE STATUS:", "$b")

        if (b) {
            _deviceConnected.postValue(b)
        } else {
            _showErrorDeviceConnected.postValue(true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        device.disconnect()
    }

    override fun handleTagdata(tagData: Array<TagData?>?) {
        readNumber= localSharedPreferences!!.getReadNumber()
        val code = tagData?.get(0)?.tagID.toString()

        Log.e("handleTagdata","$code")
        try {

            if( tagData?.size!! > 1){

                _showErrorNumberTagsDetected.postValue(true)

            }else{
                if(this.writeEnable){
                    deviceInstanceRFID!!.writeTagMode(this.epc!!,code)
                }else{
                    tagData!!.iterator().forEachRemaining {
                        CoroutineScope(Dispatchers.IO).launch {
                            newTag(it!!.tagID.toString(),readNumber)
                        }
                    }
                }
            }






        } catch (ex: Exception) {

        }

    }

    private suspend fun newTag(epc:String, readNumb:Int): Tags = withContext(Dispatchers.IO) {
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

    override fun handleTriggerPress(pressed: Boolean) {
        Log.e("handleTriggerPress", "${pressed}")


        if(pressed){
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
        Log.e("BatteryLevel", "${level}")
    }

    override fun setDataBarCode(code: String) {
        _liveCode.value = code.filter {it in '0'..'9'}
    }

    override fun connected(status: Boolean) {
        TODO("Not yet implemented")
    }

    override fun writingTagStatus(status: Boolean) {
        Log.e("writingTagStatus", "${status}")
        this.writeEnable = false

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        navController.navigate(R.id.optionsWriteFragment)
        _liveCode.value = ""

    }

    @SuppressLint("MissingPermission")
    private fun searchDevices(){

        Log.e("searchDevices()",".....")
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

                    /*
                    dialogSelectPairDevices = DialogSelectPairDevices(
                        this@OptionsWriteFragment,
                        devicesRFID
                    )
                    dialogSelectPairDevices.show()*/
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
/*
        dialogLoaderHandHeld = CustomDialogLoader(
            this@OptionsWriteFragment,
            TypeLoading.BLUETOOTH_DEVICE
        )*/
    }
    fun resetBarCode(){
        _liveCode.value=""
    }
    private fun createDeviceInstance(deviceName: String){

         device = Device(this,deviceName,this)
         device.connect()
    }


}