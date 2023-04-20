package com.checkpoint.rfid_raw_material.ui.handheld

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SeekBar
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.checkpoint.rfid_raw_material.MainActivity
import com.checkpoint.rfid_raw_material.R
import com.checkpoint.rfid_raw_material.databinding.FragmentHandHeldConfigBinding
import com.checkpoint.rfid_raw_material.pojos.ConfigLongValues

class HandHeldConfigFragment : Fragment() {

    private lateinit var viewModel: HandHeldConfigViewModel
    private var _binding: FragmentHandHeldConfigBinding? = null
    private var activityMain: MainActivity? = null

    private val binding get() = _binding!!
     private var sessionSelected = 0

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
         val readNumber = arguments?.getInt("readNumber")

        viewModel = ViewModelProvider(this)[HandHeldConfigViewModel::class.java]
        _binding = FragmentHandHeldConfigBinding.inflate(inflater, container, false)
        activityMain = requireActivity() as MainActivity
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val bundle = bundleOf(
                "readNumber" to readNumber
            )
            findNavController().navigate(R.id.pagerFragment, bundle)
        }

        var config = viewModel.getConfigFromPreferences()
        val currentPower= config.first!!
        val session = config.second


          activityMain!!.batteryLevel.observe(viewLifecycleOwner){
            binding.imgBattery.setPercent(it!!)
            binding.txtPercent.text = it.toString()

        }

        binding.txtPower.text = currentPower.toString()

        activityMain!!.maxPowerList.observe(viewLifecycleOwner) {
            binding.seekBarPower.max = it.size

        }
        binding.seekBarPower.progress = currentPower!!




        binding.seekBarPower.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            var startTrackeing= false
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(startTrackeing){
                    binding.txtPower.text = "$progress dbm"
                    binding.seekBarPower.progress = progress
                }

            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                startTrackeing = true
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })
        val regionList: MutableList<String> = mutableListOf()
        regionList += "SESSION_0"
        regionList += "SESSION_1"
        val adapter = ArrayAdapter(requireContext(), R.layout.items_provider, regionList)

        when(session){
            "SESSION_0"->{
                binding.listRegions.setText("SESSION_0")

            }
            "SESSION_1"->{
                binding.listRegions.setText("SESSION_1")

            }

        }
        binding.listRegions.setAdapter(adapter)

        binding.btnSetPower.setOnClickListener {

             viewModel.saveConfigToPreferences(binding.listRegions.text.toString(),
                 binding.seekBarPower.progress ).apply {
                findNavController().navigate(R.id.pagerFragment)
            }

        }
        return binding.root
    }
}