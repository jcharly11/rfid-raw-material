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
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.checkpoint.rfid_raw_material.MainActivity
import com.checkpoint.rfid_raw_material.R
import com.checkpoint.rfid_raw_material.databinding.FragmentOptionsWriteBinding
import com.checkpoint.rfid_raw_material.utils.dialogs.DialogErrorDeviceConnected
import com.checkpoint.rfid_raw_material.utils.dialogs.DialogLookingForDevice

class OptionsWriteFragment : Fragment(){

    private lateinit var viewModel: OptionsWriteViewModel
    private var _binding: FragmentOptionsWriteBinding? = null
    private val binding get() = _binding!!
    private var activityMain: MainActivity? = null
    var doubleBackPressed = false
    private var deviceName: String? = null
    private var dialogLookingForDevice: DialogLookingForDevice? = null
    private var dialogErrorDeviceConnected: DialogErrorDeviceConnected? = null





    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProvider(this)[OptionsWriteViewModel::class.java]
        _binding = FragmentOptionsWriteBinding.inflate(inflater, container, false)
        activityMain = requireActivity() as MainActivity
        dialogLookingForDevice  = DialogLookingForDevice(requireContext())
        dialogErrorDeviceConnected =  DialogErrorDeviceConnected(requireContext())

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            exitApp()
        }





        binding.btnInventory.setOnClickListener {
            val bundle = bundleOf(
                "deviceName" to deviceName
            )
            findNavController().navigate(R.id.pagerFragment, bundle)

        }
        binding.btnWriteTag.setOnClickListener {
            val bundle = bundleOf(
                "deviceName" to deviceName
            )
            findNavController().navigate(R.id.writeTagFragment, bundle)
        }
        activityMain!!.deviceConnected.observe(viewLifecycleOwner) {
            if(it){

                dialogLookingForDevice!!.dismiss()
                binding.tvDeviceSelected.text = activityMain!!.deviceName

            }

        }
        activityMain!!.showErrorDeviceConnected.observe(viewLifecycleOwner){
            if(dialogLookingForDevice!!.isShowing){
                dialogLookingForDevice!!.dismiss()
             }

            //binding.btnInventory.isEnabled = false
            //binding.btnWriteTag.isEnabled = false
            dialogErrorDeviceConnected!!.show()


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

        dialogLookingForDevice!!.show()
        activityMain!!.startDeviceConnection()


        enableBarButtons()
    }




    fun enableBarButtons(){
        activityMain!!.batteryView!!.visibility = View.INVISIBLE
        activityMain!!.btnHandHeldGun!!.visibility = View.INVISIBLE
        activityMain!!.btnCreateLog!!.visibility = View.INVISIBLE
        activityMain!!.lyCreateLog!!.visibility = View.INVISIBLE
    }




}