package com.checkpoint.rfid_raw_material.ui.write

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.checkpoint.rfid_raw_material.MainActivity
import com.checkpoint.rfid_raw_material.R
import com.checkpoint.rfid_raw_material.databinding.FragmentConfirmWriteTagBinding
import com.checkpoint.rfid_raw_material.utils.dialogs.DialogErrorDeviceConnected
import com.checkpoint.rfid_raw_material.utils.dialogs.DialogPrepareTrigger
import com.checkpoint.rfid_raw_material.utils.dialogs.DialogWaitForHandHeld
import com.checkpoint.rfid_raw_material.utils.dialogs.DialogWriteTagConfirmation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ConfirmWriteTagFragment : Fragment() {

    private lateinit var viewModel: ConfirmWriteTagViewModel
    private var _binding: FragmentConfirmWriteTagBinding? = null
    private val binding get() = _binding!!
    private lateinit var dialogWaitForHandHeld: DialogWaitForHandHeld
    private lateinit var dialogWriteTagConfirmation: DialogWriteTagConfirmation
    private lateinit var dialogPrepareTrigger: DialogPrepareTrigger
    private lateinit var dialogErrorDeviceConnected: DialogErrorDeviceConnected
    private var startDevice: Boolean = false
    private var readNumber: Int? = 0
    private var activityMain: MainActivity? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val epc = arguments?.getString("epc")
        readNumber = arguments?.getInt("readNumber")

        viewModel = ViewModelProvider(this)[ConfirmWriteTagViewModel::class.java]
        _binding = FragmentConfirmWriteTagBinding.inflate(inflater, container, false)
        activityMain = requireActivity() as MainActivity

        activityMain!!.lyCreateLog!!.visibility = View.GONE
        dialogWaitForHandHeld = DialogWaitForHandHeld(this@ConfirmWriteTagFragment)
        dialogPrepareTrigger = DialogPrepareTrigger(this@ConfirmWriteTagFragment)
        dialogWriteTagConfirmation =  DialogWriteTagConfirmation(this@ConfirmWriteTagFragment,Pair("",""))
        dialogErrorDeviceConnected= DialogErrorDeviceConnected(this@ConfirmWriteTagFragment)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {}

        viewModel.liveTID.observe(viewLifecycleOwner){

            Log.e("observe", it)
            binding.tvTID.setText(it)

        }
        viewModel.readyToRead.observe(viewLifecycleOwner){


            Log.e("readyToRead","$it")
            if(!it && startDevice ){
                dialogWaitForHandHeld.dismiss()
                binding.btnWrite.isEnabled = false
                binding.tvTID.isEnabled = false
                binding.edtTagEPC.isEnabled = false
                dialogErrorDeviceConnected.show()
                startDevice=false
            }else{
                dialogWaitForHandHeld.dismiss()

            }


        }
        viewModel.writeComplete.observe(viewLifecycleOwner){
            if(it){
                lifecycleScope.launch {
                    dialogPrepareTrigger.dismiss()
                    viewModel.disconectDevice()
                    val bundle = bundleOf(
                        "readNumber" to readNumber
                    )
                    findNavController().navigate(R.id.writeTagFragment,bundle)
                }
            }
        }

        binding.tvTID.setOnFocusChangeListener { _, b ->
            if(b){
                dialogWaitForHandHeld.show()
                lifecycleScope.launch {
                    delay(5000)
                    viewModel.initReaderRFID()
                    startDevice=true

                }

            }
        }

        binding.edtTagEPC.setText(epc)
        binding.btnWrite.setOnClickListener {
             lifecycleScope.launch{
                val tid = binding.tvTID.text.toString()
                val epcTag = binding.edtTagEPC.text.toString()

                viewModel.prepareToWrite(tid,epcTag,"").apply {

                    Log.e("prepareToWrite","$this")

                    if(this){
                        dialogPrepareTrigger.show()
                    }
                }
            }
        }

        binding.btnCancel.setOnClickListener {
            binding.tvTID.setText("")
            binding.edtTagEPC.setText("")
            startDevice= false
            val bundle = bundleOf(
                "readNumber" to readNumber
            )
            //findNavController().popBackStack()
            findNavController().navigate(R.id.writeTagFragment,bundle)
        }
        return binding.root
    }


}