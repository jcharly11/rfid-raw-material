package com.checkpoint.rfid_raw_material.ui.license

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.checkpoint.rfid_raw_material.R
import com.checkpoint.rfid_raw_material.databinding.FragmentHandHeldConfigBinding
import com.checkpoint.rfid_raw_material.databinding.FragmentLicenseLoadBinding
import com.checkpoint.rfid_raw_material.ui.handheld.HandHeldConfigViewModel

private lateinit var viewModel: LicenseLoadViewModel
private var _binding: FragmentLicenseLoadBinding? = null

private val binding get() = _binding!!
class LicenseLoadFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProvider(this)[LicenseLoadViewModel::class.java]
        _binding = FragmentLicenseLoadBinding.inflate(inflater, container, false)
        binding.imgCopy.setOnClickListener {

        }

        return binding.root
    }


}