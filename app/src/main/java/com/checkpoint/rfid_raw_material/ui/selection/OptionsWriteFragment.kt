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
import com.checkpoint.rfid_raw_material.handheld.kt.Device
import com.checkpoint.rfid_raw_material.handheld.kt.interfaces.DeviceConnectStatusInterface
import com.checkpoint.rfid_raw_material.utils.dialogs.CustomDialogLoader
import com.checkpoint.rfid_raw_material.utils.dialogs.DialogErrorDeviceConnected
import com.checkpoint.rfid_raw_material.utils.dialogs.DialogSelectPairDevices
import com.checkpoint.rfid_raw_material.utils.dialogs.DialogWaitForHandHeld
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


class OptionsWriteFragment : Fragment(){

    private lateinit var viewModel: OptionsWriteViewModel
    private var _binding: FragmentOptionsWriteBinding? = null
    private val binding get() = _binding!!
    private var activityMain: MainActivity? = null
    var doubleBackPressed = false
   private var deviceName: String? = null





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



        binding.tvDeviceSelected.text = activityMain!!.deviceName
        binding.btnInventory.setOnClickListener {
            val bundle = bundleOf(
                "deviceName" to deviceName
            )


            activityMain!!.startRFIDReadInstance(false,"")

            findNavController().navigate(R.id.pagerFragment, bundle)
        }
        binding.btnWriteTag.setOnClickListener {
            val bundle = bundleOf(
                "deviceName" to deviceName
            )
            activityMain!!.startBarCodeReadInstance()
            findNavController().navigate(R.id.writeTagFragment, bundle)
        }


        return binding.root
    }



        private fun exitApp() {
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




    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).supportActionBar!!.show()
    }








}