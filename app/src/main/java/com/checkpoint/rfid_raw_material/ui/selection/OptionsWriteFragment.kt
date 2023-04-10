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
import kotlinx.coroutines.CoroutineScope


class OptionsWriteFragment : Fragment(), SelectDeviceDialogInterface {

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

    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProvider(this)[OptionsWriteViewModel::class.java]
        _binding = FragmentOptionsWriteBinding.inflate(inflater, container, false)
        activityMain = requireActivity() as MainActivity

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


        Log.e("DEVICE SELECTED ", "$deviceName")
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        activityMain!!.batteryView!!.visibility = View.GONE
        activityMain!!.btnHandHeldGun!!.visibility = View.GONE
        activityMain!!.lyCreateLog!!.visibility = View.GONE
    }


    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).supportActionBar!!.show()
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


    fun exitApp() {
        if (doubleBackPressed) {
            System.exit(0)
        }
        doubleBackPressed = true
        Toast.makeText(context, resources.getText(R.string.press_back_again), Toast.LENGTH_SHORT)
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