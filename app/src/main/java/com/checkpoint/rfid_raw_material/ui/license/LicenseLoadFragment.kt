package com.checkpoint.rfid_raw_material.ui.license

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.checkpoint.rfid_raw_material.R
import com.checkpoint.rfid_raw_material.databinding.FragmentHandHeldConfigBinding
import com.checkpoint.rfid_raw_material.databinding.FragmentLicenseLoadBinding
import com.checkpoint.rfid_raw_material.enums.TypeWarning
import com.checkpoint.rfid_raw_material.ui.handheld.HandHeldConfigViewModel
import com.checkpoint.rfid_raw_material.utils.dialogs.CustomDialogLicense
import com.checkpoint.rfid_raw_material.utils.dialogs.interfaces.CustomDialogLicenseInterface
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LicenseLoadFragment : Fragment(), CustomDialogLicenseInterface {


    private lateinit var viewModel: LicenseLoadViewModel
    private var _binding: FragmentLicenseLoadBinding? = null
    private lateinit var customDialogLicense: CustomDialogLicense


    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProvider(this)[LicenseLoadViewModel::class.java]
        _binding = FragmentLicenseLoadBinding.inflate(inflater, container, false)

        var tokenLicense= viewModel.getTokenLicense()
        binding.tvLicense.setText(tokenLicense)

        validateLicense()

        binding.imgCopy.setOnClickListener {
            viewModel.copyToClpBoard(binding.tvIdDevice.toString())
        }
        binding.btnSetLicense.setOnClickListener {
            this.lifecycleScope.launch {
                viewModel.validateLicense(binding.tvLicense.text.toString()).apply {

                    if (this){
                        findNavController().navigate(R.id.optionsWriteFragment)
                    }

                }
            }

        }
        viewModel.idDevice.observe(viewLifecycleOwner){
            binding.tvIdDevice.text = it
        }

        return binding.root
    }


    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).supportActionBar!!.hide()

    }
    fun validateLicense(){
        this.lifecycleScope.launch {
            var tvLicense= binding.tvLicense.text.toString()
            viewModel.validateLicense(tvLicense).apply {
                if (this){
                    viewModel.setTokenLicense(tvLicense)
                    findNavController().navigate(R.id.optionsWriteFragment)
                }
                else {
                    if(!tvLicense.isNullOrEmpty()) {
                        customDialogLicense =
                            CustomDialogLicense(this@LicenseLoadFragment, TypeWarning.WRONG_TOKEN)
                        customDialogLicense.show()
                    }
                    viewModel.setTokenLicense("")
                }
            }
        }
    }

    override fun closeDialog() {
        customDialogLicense.dismiss()
    }
}