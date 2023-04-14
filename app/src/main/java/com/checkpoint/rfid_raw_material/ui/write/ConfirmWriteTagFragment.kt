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
import com.checkpoint.rfid_raw_material.utils.dialogs.*
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
    private lateinit var dialogErrorMultipleTags: DialogErrorMultipleTags
    private lateinit var dialogPrepareReading: DialogPrepareReading
    private var startDevice: Boolean = false
    private var readNumber: Int? = 0
    private var activityMain: MainActivity? = null
    private var tid: String? = null
    private var deviceName: String? = null
    private var epc: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        epc = arguments?.getString("epc")
        readNumber = arguments?.getInt("readNumber")
        deviceName = arguments?.getString("deviceName")


        viewModel = ViewModelProvider(this)[ConfirmWriteTagViewModel::class.java]
        _binding = FragmentConfirmWriteTagBinding.inflate(inflater, container, false)
        activityMain = requireActivity() as MainActivity

        activityMain!!.lyCreateLog!!.visibility = View.GONE
        dialogWaitForHandHeld = DialogWaitForHandHeld(this@ConfirmWriteTagFragment)
        dialogPrepareTrigger = DialogPrepareTrigger(this@ConfirmWriteTagFragment)
        dialogWriteTagConfirmation =  DialogWriteTagConfirmation(this@ConfirmWriteTagFragment,Pair("",""))
        dialogErrorDeviceConnected= DialogErrorDeviceConnected(this@ConfirmWriteTagFragment)
        dialogErrorMultipleTags = DialogErrorMultipleTags(this@ConfirmWriteTagFragment)
        dialogPrepareReading = DialogPrepareReading(this@ConfirmWriteTagFragment)

        binding.edtTagEPC.setText(epc)
        binding.edtTagEPC.isEnabled = false

        binding.btnCancel.setOnClickListener {
            tid=""
            binding.edtTagEPC.setText("")
            startDevice= false
            val bundle = bundleOf(
                "readNumber" to readNumber
            )
            findNavController().navigate(R.id.writeTagFragment,bundle)
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
       activityMain!!.stopReadedBarCode()
        activityMain!!.startRFIDReadInstance(true,this.epc!!)

    }

}