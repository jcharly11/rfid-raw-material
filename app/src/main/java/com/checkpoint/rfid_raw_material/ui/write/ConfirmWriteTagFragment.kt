package com.checkpoint.rfid_raw_material.ui.write

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.checkpoint.rfid_raw_material.MainActivity
import com.checkpoint.rfid_raw_material.R
import com.checkpoint.rfid_raw_material.databinding.FragmentConfirmWriteTagBinding
import com.checkpoint.rfid_raw_material.utils.dialogs.*
import com.checkpoint.rfid_raw_material.utils.dialogs.interfaces.DialogWriteTagSuccessInterface

class ConfirmWriteTagFragment : Fragment(), DialogWriteTagSuccessInterface {

    private var _binding: FragmentConfirmWriteTagBinding? = null
    private val binding get() = _binding!!
    private var startDevice: Boolean = false
    private var readNumber: Int? = 0
    private var activityMain: MainActivity? = null
    private var tid: String? = null
    private var deviceName: String? = null
    private var epc: String? = null
    private var version: String? = null
    private var subversion: String? = null
    private var type: String? = null
    private var identifier: String? = null
    private var provider: Int? = null
    private var dialogErrorMultipleTags: DialogErrorMultipleTags? = null
    private var dialogWriteTag: CustomDialogWriteTag? = null
    private var dialogLoadingWrite: DialogPrepareTrigger? = null
    private var dialogERRORWriting: DialogErrorWritingTag? = null
    private var dialogWriteTagSuccess: DialogWriteTagSuccess? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        epc = arguments?.getString("epc")
        version = arguments?.getString("version")
        subversion = arguments?.getString("subversion")
        type = arguments?.getString("type")
        identifier = arguments?.getString("identifier")
        provider = arguments?.getInt("provider")



        Log.e("GENERATED EPC","$epc")
        readNumber = arguments?.getInt("readNumber")
        deviceName = arguments?.getString("deviceName")


        _binding = FragmentConfirmWriteTagBinding.inflate(inflater, container, false)
        activityMain = requireActivity() as MainActivity
        dialogErrorMultipleTags = DialogErrorMultipleTags(this@ConfirmWriteTagFragment)
        dialogWriteTag = CustomDialogWriteTag(this@ConfirmWriteTagFragment)
        dialogLoadingWrite = DialogPrepareTrigger(this@ConfirmWriteTagFragment)
        dialogERRORWriting = DialogErrorWritingTag(this@ConfirmWriteTagFragment)
        activityMain!!.lyCreateLog!!.visibility = View.GONE
        activityMain!!.version= version!!
        activityMain!!.subVersion= subversion!!
        activityMain!!.type= type!!
        activityMain!!.identifier= identifier!!
        activityMain!!.provider= provider!!


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

        activityMain!!.showDialogWritingTag.observe(viewLifecycleOwner){
            if(it){
                dialogLoadingWrite!!.show()
            }else{
                dialogLoadingWrite!!.dismiss()
            }
        }

        activityMain!!.showErrorNumberTagsDetected.observe(viewLifecycleOwner){
            if(it){
                dialogErrorMultipleTags!!.show()
            }
        }

        activityMain!!.showDialogWritingError.observe(viewLifecycleOwner){
            if (it){
                dialogERRORWriting!!.show()
            }

        }
        activityMain!!.showDialogWritingSuccess.observe(viewLifecycleOwner){
            if(it){
                dialogWriteTagSuccess = DialogWriteTagSuccess(this@ConfirmWriteTagFragment,epc)
                dialogWriteTagSuccess!!.show()
            }

        }

        return binding.root
    }

    override fun successRecording() {
        dialogWriteTagSuccess!!.dismiss()
        activityMain!!.restartWritingFlags()
        val bundle = bundleOf(
            "readNumber" to readNumber
        )

         findNavController().navigate(R.id.writeTagFragment, bundle)
     }

    override fun onStart() {
        super.onStart()
        activityMain!!.stopReadedBarCode()
        activityMain!!.startRFIDReadInstance(true,this.epc!!)

    }

}