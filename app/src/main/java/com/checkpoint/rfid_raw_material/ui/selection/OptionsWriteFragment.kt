package com.checkpoint.rfid_raw_material.ui.selection

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.checkpoint.rfid_raw_material.MainActivity
import com.checkpoint.rfid_raw_material.R
import com.checkpoint.rfid_raw_material.bluetooth.BluetoothHandler
import com.checkpoint.rfid_raw_material.databinding.FragmentOptionsWriteBinding
import com.checkpoint.rfid_raw_material.enums.TypeLoading
import com.checkpoint.rfid_raw_material.utils.dialogs.CustomDialogLoader
import com.checkpoint.rfid_raw_material.utils.dialogs.DialogErrorDeviceConnected
import com.checkpoint.rfid_raw_material.utils.dialogs.DialogSelectPairDevices
import com.checkpoint.rfid_raw_material.utils.dialogs.interfaces.SelectDeviceDialogInterface
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.coroutines.flow
import com.fondesa.kpermissions.coroutines.sendSuspend
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.isDenied
import com.fondesa.kpermissions.isGranted
import com.fondesa.kpermissions.request.PermissionRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class OptionsWriteFragment : Fragment(), SelectDeviceDialogInterface, PermissionRequest.Listener {

    private lateinit var viewModel: OptionsWriteViewModel
    private var _binding: FragmentOptionsWriteBinding? = null
    private val binding get() = _binding!!
    private var activityMain: MainActivity? = null
    var doubleBackPressed = false
    private lateinit var dialogErrorDeviceConnected: DialogErrorDeviceConnected
    private lateinit var dialogSelectPairDevices: DialogSelectPairDevices
    private var bluetoothHandler: BluetoothHandler? = null
    private var deviceName: String? = null


    private lateinit var dialogLoaderHandHeld: CustomDialogLoader

    private val requestPermissions by lazy {
        permissionsBuilder(Manifest.permission.BLUETOOTH_CONNECT,Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_SCAN,Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION).build()
    }

    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProvider(this)[OptionsWriteViewModel::class.java]
        _binding = FragmentOptionsWriteBinding.inflate(inflater, container, false)
        activityMain = requireActivity() as MainActivity
        requestPermissions.addListener(this)


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            exitApp()
        }



        binding.btnInventory.setOnClickListener {
            val bundle = bundleOf(
                "deviceName" to deviceName
            )
            findNavController().navigate(R.id.inventoryPagerFragment, bundle)
        }
        binding.btnWriteTag.setOnClickListener {
            val bundle = bundleOf(
                "deviceName" to deviceName
            )
            findNavController().navigate(R.id.writeTagFragment, bundle)
        }

        requestPermissions.send()

        Log.e("DEVICE SELECTED ", "$deviceName")
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        activityMain!!.batteryView!!.visibility = View.GONE
        activityMain!!.btnHandHeldGun!!.visibility = View.GONE
        activityMain!!.lyCreateLog!!.visibility = View.GONE
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

        if(res>= 4)
            searchDevices()
        else
            Toast.makeText(context, R.string.accept_permissions, Toast.LENGTH_SHORT).show()
    }


    @SuppressLint("MissingPermission")
    fun searchDevices(){

        dialogErrorDeviceConnected = DialogErrorDeviceConnected(this@OptionsWriteFragment)
        bluetoothHandler = BluetoothHandler(requireContext())
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

                    dialogSelectPairDevices = DialogSelectPairDevices(
                        this@OptionsWriteFragment,
                        devicesRFID
                    )
                    dialogSelectPairDevices.show()
                } else {

                    if (devicesRFID.isNotEmpty()) {

                        deviceName = devicesRFID[0]
                    } else {
                        dialogErrorDeviceConnected.show()

                    }
                }

            } else {
                dialogErrorDeviceConnected.show()
                // DIALOG TURN ON BLUETOOTH
            }

        }

        dialogLoaderHandHeld = CustomDialogLoader(
            this@OptionsWriteFragment,
            TypeLoading.BLUETOOTH_DEVICE
        )
    }



    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).supportActionBar!!.show()
    }


        fun exitApp() {
            if (doubleBackPressed) {
                System.exit(0)
            }
            doubleBackPressed = true
            Toast.makeText(
                context,
                resources.getText(R.string.press_back_again),
                Toast.LENGTH_SHORT
            )
                .show()

            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                doubleBackPressed = false
            }, 2000)
        }

        override fun setDevice(device: String) {
            Log.e("DEVICE SELECTED:", "$device")
            deviceName = device
            dialogSelectPairDevices.dismiss()
        }


}