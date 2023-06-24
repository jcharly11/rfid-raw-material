package com.checkpoint.rfid_raw_material

import CustomDialogRemoveProvider
import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.os.bundleOf
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import com.checkpoint.rfid_raw_material.bluetooth.BluetoothHandler
import com.checkpoint.rfid_raw_material.databinding.ActivityBarCodeBinding
import com.checkpoint.rfid_raw_material.databinding.ActivityConfirmWriteBinding
import com.checkpoint.rfid_raw_material.handheld.kt.Device
import com.checkpoint.rfid_raw_material.handheld.kt.DeviceInstanceBARCODE
import com.checkpoint.rfid_raw_material.handheld.kt.DeviceInstanceRFID
import com.checkpoint.rfid_raw_material.handheld.kt.interfaces.BarcodeHandHeldInterface
import com.checkpoint.rfid_raw_material.handheld.kt.interfaces.BatteryHandlerInterface
import com.checkpoint.rfid_raw_material.handheld.kt.interfaces.DeviceConnectStatusInterface
import com.checkpoint.rfid_raw_material.handheld.kt.interfaces.UnavailableDeviceInterface
import com.checkpoint.rfid_raw_material.preferences.LocalPreferences
import com.checkpoint.rfid_raw_material.source.DataRepository
import com.checkpoint.rfid_raw_material.source.RawMaterialsDatabase
import com.checkpoint.rfid_raw_material.source.db.Provider
import com.checkpoint.rfid_raw_material.source.model.ProviderModel
import com.checkpoint.rfid_raw_material.ui.handheld.HandHeldConfigFragment
import com.checkpoint.rfid_raw_material.utils.Conversor
import com.checkpoint.rfid_raw_material.utils.dialogs.CustomDialogProvider
import com.checkpoint.rfid_raw_material.utils.dialogs.DialogErrorDeviceConnected
import com.checkpoint.rfid_raw_material.utils.dialogs.DialogLookingForDevice
import com.checkpoint.rfid_raw_material.utils.dialogs.interfaces.CustomDialogProviderInterface
import com.checkpoint.rfid_raw_material.utils.dialogs.interfaces.CustomDialogRemoveProviderInterface
import com.zebra.rfid.api3.ReaderDevice
import com.zebra.rfid.api3.Readers
import io.sentry.Sentry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BarCodeActivity : ActivityBase(),
    DeviceConnectStatusInterface,
    Readers.RFIDReaderEventHandler,
    BarcodeHandHeldInterface,
    UnavailableDeviceInterface,
    CustomDialogProviderInterface,
    CustomDialogRemoveProviderInterface
{
    private var bluetoothHandler: BluetoothHandler? = null
    private lateinit var binding: ActivityBarCodeBinding
    private var deviceName: String? = null
    private var dialogProvider: CustomDialogProvider? = null

    var deviceInstanceBARCODE: DeviceInstanceBARCODE? = null
    private lateinit var dialogRemoveProvider: CustomDialogRemoveProvider



    var idProvider: Int = 0
    var idSupplier = String()
    var repository: DataRepository? = null

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarCodeBinding.inflate(layoutInflater)
        dialogErrorDeviceConnected = DialogErrorDeviceConnected(this)
        dialogLookingForDevice= DialogLookingForDevice(this)
        dialogRemoveProvider = CustomDialogRemoveProvider(this,this)
        dialogProvider = CustomDialogProvider(this, this)
        localSharedPreferences = LocalPreferences(application)



        setContentView(binding.root)
        setSupportActionBar(binding.appRawMaterials.toolbar)

        btnCreateLog = binding.appRawMaterials.imgCreateLog
        batteryView = binding.appRawMaterials.batteryView
        lyCreateLog = binding.appRawMaterials.lyCreateLog
        btnHandHeldGun = binding.appRawMaterials.imgHandHeldGun

        batteryView!!.visibility = View.GONE
        btnHandHeldGun!!.visibility = View.GONE
        lyCreateLog!!.visibility = View.GONE


         bluetoothHandler = BluetoothHandler(this)

        bluetoothHandler!!.list()!!.forEach {
            if (it.name.contains("RFD8500")) {
                deviceName += it.name
            }
        }
        device = Device(applicationContext,deviceName!!,this)
        repository = DataRepository.getInstance(
            RawMaterialsDatabase.getDatabase(applicationContext)

        )
        repository!!.getProviders().observe(this) {
             var listProviders: MutableList<ProviderModel> = mutableListOf()


             it.iterator().forEachRemaining {
                 listProviders.add(ProviderModel(it.id, it.idAS, it.name))
             }

             if (listProviders.size == 0)
                 binding.btnRemoveProvider.visibility = View.GONE
             else
                 binding.btnRemoveProvider.visibility = View.VISIBLE

             val adapter: ArrayAdapter<ProviderModel> =
                 ArrayAdapter<ProviderModel>(
                     this,
                     R.layout.simple_spinner_dropdown_item,
                     listProviders
                 )


             binding.spProviderList.adapter = adapter
             binding.spProviderList.onItemSelectedListener =
                 object : AdapterView.OnItemSelectedListener {
                     override fun onItemSelected(
                         parent: AdapterView<*>?,
                         view: View?,
                         position: Int,
                         id: Long
                     ) {
                         idProvider = listProviders[position].id
                         idSupplier = listProviders[position].idAS!!
                     }

                     override fun onNothingSelected(p0: AdapterView<*>?) {
                     }
                 }
         }
        binding.btnRemoveProvider.setOnClickListener {
            dialogRemoveProvider.show()
        }

        binding.btnFinishWrite.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }
        binding.btnWriteTag.setOnClickListener {
            try {
                CoroutineScope(Dispatchers.Main).launch {
                    val versionValue = binding.tvVersion.text.toString()
                    val subversionValue = binding.tvSubversion.text.toString()
                    val typeValue = binding.tvType.text.toString()
                    val pieceValue = binding.tvIdentifier.text.toString()

                    if (versionValue.isNotEmpty() &&
                        subversionValue.isNotEmpty() &&
                        typeValue.isNotEmpty() && pieceValue.isNotEmpty() &&
                        idProvider > 0
                    ) {

                       calculateEPC(
                            versionValue,
                            subversionValue,
                            typeValue,
                            idProvider.toString(),
                            pieceValue
                        ).let {
                           val intent = Intent(applicationContext, ConfirmWriteActivity::class.java)
                           intent.putExtra("epc", it)
                           device!!.disconnect().let {
                               startForResult.launch(intent)

                           }

                        }


                    } else {

                       // dialogErrorEmptyFields.show()
                    }

                }

            } catch (ex: Exception) {
                Sentry.captureMessage("${ex.message}")
                Log.e("logError", ex.toString())
            }
        }

        binding.btnAddProvider.setOnClickListener {
           dialogProvider!!.show()
        }


    }
    fun providerDialogShow(){
        runOnUiThread {


            dialogProvider!!.show()

        }
    }
    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            binding.tvIdentifier.setText(String())



        }
    }
    override fun onStart() {
        super.onStart()
        dialogLookingForDevice!!.show()
        device!!.connect()

    }

    override fun onDestroy() {
        super.onDestroy()
        device!!.disconnect()
    }
    override fun setDataBarCode(code: String) {
        binding.tvIdentifier.setText(code)
    }

    override fun connected(status: Boolean) {
        TODO("Not yet implemented")
    }

    override fun isConnected(b: Boolean) {
        dialogLookingForDevice!!.dismiss()
        if(b){
            deviceInstanceBARCODE = DeviceInstanceBARCODE(device!!.getReaderDevice(), applicationContext)
            deviceInstanceBARCODE!!.setBarCodeHandHeldInterface(this)

        }else{
            dialogErrorDeviceConnected!!.show()
        }
    }

    override fun RFIDReaderAppeared(p0: ReaderDevice?) {
        TODO("Not yet implemented")
    }

    override fun RFIDReaderDisappeared(p0: ReaderDevice?) {
        TODO("Not yet implemented")
    }


    override fun deviceCharging() {
        TODO("Not yet implemented")
    }
    private fun calculateEPC(versionValue: String,
                             subversionValue: String,
                             typeValue: String,
                             idProvider: String, pieceValue: String):String {
        Log.e("calculateEPC supplier","$idProvider")

        val conversor = Conversor()
        var hexValueEpc = ""
        val version = conversor.toBinaryString(versionValue, 5, '0')
        val subVersion = conversor.toBinaryString(subversionValue, 5, '0')
        val type = conversor.toBinaryString(typeValue, 6, '0')
        val supplier = conversor.toBinaryString(idProvider.toString().trim(), 32, '0')
        val piece = conversor.toBinaryString(pieceValue, 80, '0')

        val binaryChain = "$version$type$subVersion$piece$supplier"
       return  conversor.groupBytes(binaryChain)

    }

    override fun saveProvider(idProvider: String,idASProvider: String,nameProvider: String) {

        if (idProvider.isNotEmpty() && idASProvider.isNotEmpty() && nameProvider.isNotEmpty()) {
            CoroutineScope(Dispatchers.Main).launch {
                try {

                    newProvider(idProvider.toInt(), idASProvider, nameProvider)
                }
                catch (ex: Exception){
                    //TODO
                }
            }
            binding.btnRemoveProvider.visibility = View.VISIBLE

            closeDialog()
        } else
            Toast.makeText(
                this,
                "Validate all fields", Toast.LENGTH_SHORT
            ).show()
    }

    suspend fun newProvider(id: Int,idAS:String,nameProvider:String): Provider = withContext(Dispatchers.IO) {
        repository!!.insertNewProvider(
            Provider(
                0,
                id,
                idAS,
                nameProvider
            )
        )
    }

    override fun closeDialog() {
        dialogProvider!!.dismiss()
    }

    override fun closeDialogRemoveProvider() {
        dialogRemoveProvider!!.dismiss()
    }

    override fun removeProvider() {
        CoroutineScope(Dispatchers.Main).launch {
            deleteProvider(idProvider)
            dialogRemoveProvider.dismiss()
        }
    }

    suspend fun deleteProvider(idProvider: Int) = withContext(Dispatchers.IO){
        repository!!.deleteProvider(idProvider)
    }
}