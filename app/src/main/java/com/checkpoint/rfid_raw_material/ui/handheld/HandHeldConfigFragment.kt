package com.checkpoint.rfid_raw_material.ui.handheld

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.SeekBar
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.checkpoint.rfid_raw_material.ConfirmWriteActivity
import com.checkpoint.rfid_raw_material.MainActivity
import com.checkpoint.rfid_raw_material.R
import com.checkpoint.rfid_raw_material.databinding.FragmentHandHeldConfigBinding


class HandHeldConfigFragment : Fragment() {

    private lateinit var viewModel: HandHeldConfigViewModel
    private var _binding: FragmentHandHeldConfigBinding? = null

    private val binding get() = _binding!!
    private var sessionSelected = 0

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val readNumber = arguments?.getInt("readNumber")
        val batteryLevel = arguments?.getInt("batteryLevel")

        viewModel = ViewModelProvider(this)[HandHeldConfigViewModel::class.java]
        _binding = FragmentHandHeldConfigBinding.inflate(inflater, container, false)
        var activityMain = requireActivity() as ConfirmWriteActivity

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {

            activityMain.removeFargment()
        }

        var config = viewModel.getConfigFromPreferences()
        val currentPower= config.first!!
        val session = config.second
        var volumeHH= viewModel.getVolume()
        binding.seekBarPower.max = 270
        binding.seekBarPower.progress =currentPower!!
        binding.txtPower.text = currentPower.toString()
        binding.imgBattery.setPercent(batteryLevel!!)
        binding.txtPercent.text = batteryLevel!!.toString()

        binding.btnSetPower
        binding.swVolume.isChecked= volumeHH
        if(volumeHH==true) {
            binding.tvStatusVolume.setText(R.string.volume_text_on)
            binding.tvStatusVolume.setTextColor(R.color.navy)
        }
        else {
            binding.tvStatusVolume.setText(R.string.volume_text_off)
            binding.tvStatusVolume.setTextColor(R.color.red)
        }

        binding.seekBarPower.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                binding.txtPower.text = "$progress dbm"
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })/*

        activityMain!!.batteryLevel.observe(viewLifecycleOwner){

        }*/

        binding.txtPower.text = currentPower.toString()
/*
        activityMain!!.maxPowerList.observe(viewLifecycleOwner) {
            binding.seekBarPower.max = it.size

        }*/
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

        when(session){
            "SESSION_1"->{
                binding.listRegions.setText("SESSION_1")
            }
            "SESSION_2"->{
                binding.listRegions.setText("SESSION_2",false)
            }
            "SESSION_3"->{
                binding.listRegions.setText("SESSION_3",false)
            }
        }

        val regionList: MutableList<String> = mutableListOf()
        regionList += "SESSION_1"
        regionList += "SESSION_2"
        regionList += "SESSION_3"
        val adapter = ArrayAdapter(requireContext(), R.layout.items_provider, regionList)
        binding.listRegions.setAdapter(adapter)


        binding.btnSetPower.setOnClickListener {
            viewModel.saveConfigToPreferences(binding.listRegions.text.toString(),
                binding.seekBarPower.progress, volumeHH).apply {

                activityMain.removeFargment()




            }

        }


        binding.swVolume.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                binding.tvStatusVolume.setText(R.string.volume_text_on)
                binding.tvStatusVolume.setTextColor(R.color.navy)
                volumeHH=true
            }
            else{
                binding.tvStatusVolume.setText(R.string.volume_text_off)
                binding.tvStatusVolume.setTextColor(R.color.red)
                volumeHH=false
            }
        })

        return binding.root
    }
}