package com.checkpoint.rfid_raw_material.ui.write

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.checkpoint.rfid_raw_material.databinding.FragmentConfirmWriteTagBinding
import com.checkpoint.rfid_raw_material.utils.dialogs.DialogWaitForHandHeld
import com.checkpoint.rfid_raw_material.utils.dialogs.DialogWriteTagConfirmation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ConfirmWriteTagFragment : Fragment() {

    private lateinit var viewModel: ConfirmWriteTagViewModel
    private var _binding: FragmentConfirmWriteTagBinding? = null
    private val binding get() = _binding!!
    private var dialogWaitForHandHeld: DialogWaitForHandHeld? = null
    private var dialogWriteTagConfirmation: DialogWriteTagConfirmation? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val epc = arguments?.getString("epc")
        viewModel = ViewModelProvider(this)[ConfirmWriteTagViewModel::class.java]
        _binding = FragmentConfirmWriteTagBinding.inflate(inflater, container, false)
        dialogWaitForHandHeld = DialogWaitForHandHeld(this)

        dialogWriteTagConfirmation =  DialogWriteTagConfirmation(this,Pair("",""))

        viewModel.liveTID.observe(viewLifecycleOwner){

            Log.e("observe","${it}")
            binding.tvTID.setText(it)

        }

        binding.edtTagEPC.setText(epc)
        binding.btnWrite.setOnClickListener {
            lifecycleScope.launch{
                val tid = binding.tvTID.text.toString()
                val epc = binding.edtTagEPC.text.toString()

                viewModel.prepareToWrite(tid,epc,"").apply {
                    it

                }
            }
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        dialogWaitForHandHeld!!.show()
        lifecycleScope.launch {
            delay(5000)
            viewModel.initReaderRFID()
            dialogWaitForHandHeld!!.dismiss()

            binding.tvTID.setText("E28068940000502700022E2A")
            binding.edtTagEPC.setText("9080000000000000047971CE00000000")

        }

    }


}