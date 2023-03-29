package com.checkpoint.rfid_raw_material.ui.selection

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.checkpoint.rfid_raw_material.MainActivity
import com.checkpoint.rfid_raw_material.R
import com.checkpoint.rfid_raw_material.databinding.FragmentOptionsWriteBinding
import com.checkpoint.rfid_raw_material.enums.TypeLoading
import com.checkpoint.rfid_raw_material.utils.dialogs.CustomDialogLoader


class OptionsWriteFragment : Fragment(){

    private lateinit var viewModel: OptionsWriteViewModel
    private var _binding: FragmentOptionsWriteBinding? = null
    private val binding get() = _binding!!
    private var activityMain: MainActivity? = null
    var doubleBackPressed = false


    private lateinit var dialogLoaderHandHeld: CustomDialogLoader

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

        dialogLoaderHandHeld = CustomDialogLoader(
            this@OptionsWriteFragment,
            TypeLoading.BLUETOOTH_DEVICE
        )

        binding.btnInventory.setOnClickListener {
            findNavController().navigate(R.id.inventoryPagerFragment)
        }
        binding.btnWriteTag.setOnClickListener {
            findNavController().navigate(R.id.writeTagFragment)
        }


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
    }

    fun exitApp(){
        if(doubleBackPressed){
            System.exit(0)
        }
        doubleBackPressed=true
        Toast.makeText(context, resources.getText(R.string.press_back_again), Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            doubleBackPressed = false
        }, 2000)
    }

}