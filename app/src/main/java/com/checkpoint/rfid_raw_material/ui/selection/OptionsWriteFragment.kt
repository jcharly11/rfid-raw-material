package com.checkpoint.rfid_raw_material.ui.selection

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.checkpoint.rfid_raw_material.*
import com.checkpoint.rfid_raw_material.databinding.FragmentOptionsWriteBinding
import com.checkpoint.rfid_raw_material.utils.dialogs.DialogErrorDeviceConnected
import com.checkpoint.rfid_raw_material.utils.dialogs.DialogLookingForDevice

class OptionsWriteFragment : Fragment(){

    private lateinit var viewModel: OptionsWriteViewModel
    private var _binding: FragmentOptionsWriteBinding? = null
    private val binding get() = _binding!!
     var doubleBackPressed = false
    private var deviceName: String? = null
    private var dialogLookingForDevice: DialogLookingForDevice? = null
    private var dialogErrorDeviceConnected: DialogErrorDeviceConnected? = null





    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel = ViewModelProvider(this)[OptionsWriteViewModel::class.java]
        _binding = FragmentOptionsWriteBinding.inflate(inflater, container, false)
         dialogLookingForDevice  = DialogLookingForDevice(requireContext())
        dialogErrorDeviceConnected =  DialogErrorDeviceConnected(requireContext())


        binding.btnInventory.setOnClickListener {
            startForResultWrite.launch(Intent(requireContext(), ReadActivity::class.java))

        }
        binding.btnWriteTag.setOnClickListener {

           // activityMain!!.deviceDisconnect()
            startForResultWrite.launch(Intent(requireContext(), BarCodeActivity::class.java))

        }

        return binding.root
    }


    private val startForResultWrite = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data

            if(dialogLookingForDevice!!.isShowing){
                dialogLookingForDevice!!.dismiss()
            }


        }
    }
    private val startForResultRead = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data

            if(dialogLookingForDevice!!.isShowing){
                dialogLookingForDevice!!.dismiss()
            }


        }
    }






}