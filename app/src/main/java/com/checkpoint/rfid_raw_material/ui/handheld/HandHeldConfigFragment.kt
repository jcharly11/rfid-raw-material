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
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.checkpoint.rfid_raw_material.R
import com.checkpoint.rfid_raw_material.databinding.FragmentHandHeldConfigBinding

class HandHeldConfigFragment : Fragment() {

    private lateinit var viewModel: HandHeldConfigViewModel
    private  var _binding: FragmentHandHeldConfigBinding? = null

    private val binding get() = _binding!!
    private var maxPower: Int = 0
    private var sessionSelected: String=""

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inventoryId = arguments?.getInt("inventoryId")
        val batteryPercent = arguments?.getInt("batteryLevel")
        val currentPower = arguments?.getInt("currentPower")
        val powerLevelList = arguments?.getIntArray("transmitPowerLevelList")

        viewModel = ViewModelProvider(this)[HandHeldConfigViewModel::class.java]
        _binding = FragmentHandHeldConfigBinding.inflate(inflater, container, false)

        binding.imgBattery.setPercent(batteryPercent!!)
        val valueBattery = "${batteryPercent}%"

        binding.txtPercent.text = valueBattery
        if(powerLevelList!=null){
            binding.seekBarPower.max = powerLevelList.size
            binding.seekBarPower.progress =currentPower!!
            binding.txtPower.text = currentPower.toString()
        }

        binding.seekBarPower.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                maxPower = progress
                binding.txtPower.text = "$maxPower dbm"
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })
        val regionList : MutableList<String> = mutableListOf()
        regionList+="SESSION_0"
        regionList+="SESSION_1"
        val adapter = ArrayAdapter(requireContext(), R.layout.items_provider, regionList)
        binding.listRegions.setAdapter(adapter)
        binding.listRegions.setSelection(1)
        binding.listRegions.setOnItemClickListener { adapterView, _, i, _ ->
            sessionSelected = adapterView.getItemAtPosition(i).toString()
            Log.e("----->","" +sessionSelected)
        }
        binding.btnSetPower.setOnClickListener {
            val bundle = bundleOf(
                "inventoryId" to inventoryId,
                "needTag" to true,
                "maxPower" to maxPower,
                "session" to sessionSelected)
            findNavController().navigate(R.id.inventoryPagerFragment,bundle)
        }
        return binding.root
    }
}