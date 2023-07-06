package com.checkpoint.rfid_raw_material.ui.handheld

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.SeekBar
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.checkpoint.rfid_raw_material.*
import com.checkpoint.rfid_raw_material.databinding.FragmentHandHeldConfigBinding


class HandHeldConfigFragment : Fragment() {

    private lateinit var viewModel: HandHeldConfigViewModel
    private var _binding: FragmentHandHeldConfigBinding? = null

    private val binding get() = _binding!!
    private var sessionSelected = 0
    private var activityMain:ActivityBase? = null

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val readNumber = arguments?.getInt("readNumber")
        val batteryLevel = arguments?.getInt("batteryLevel")
        val activity = arguments?.getString("activity")


        viewModel = ViewModelProvider(this)[HandHeldConfigViewModel::class.java]
        _binding = FragmentHandHeldConfigBinding.inflate(inflater, container, false)
        var config = viewModel.getConfigFromPreferences()
        val currentPower= config.first!!
        val session = config.second
        var volumeHH= viewModel.getVolume()

        when(activity){
            "ConfirmWriteActivity"->{
               val  confirmWriteActivity = requireActivity() as ConfirmWriteActivity
                requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {

                    confirmWriteActivity!!.removeFargment()
                }
                binding.btnSetPower.setOnClickListener {
                    viewModel.saveConfigToPreferences(binding.listRegions.text.toString(),
                        binding.seekBarPower.progress, volumeHH).apply {

                        confirmWriteActivity.removeFargment()


                    }

                }


            }
            "ReadActivity"->{
                 val readActivity = requireActivity() as ReadActivity
                requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                    readActivity.removeFargment()
                }
                binding.btnSetPower.setOnClickListener {
                    viewModel.saveConfigToPreferences(binding.listRegions.text.toString(),
                        binding.seekBarPower.progress, volumeHH).apply {
                        readActivity.removeFargment()

                    }

                }

            }

        }




        binding.seekBarPower.max = 270
        binding.seekBarPower.progress =currentPower!!
        binding.txtPower.text = currentPower.toString()
        binding.imgBattery.setPercent(batteryLevel!!)
        binding.txtPercent.text = batteryLevel!!.toString() +" %"

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
                binding.listRegions.setText("SESSION_2")
            }
            "SESSION_3"->{
                binding.listRegions.setText("SESSION_3")
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
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main, menu)
    }

    override fun onStart() {
        super.onStart()
        var regionList = listOf<String>()
        regionList += "SESSION_1"
        regionList += "SESSION_2"
        regionList += "SESSION_3"
        ArrayAdapter(requireContext(), R.layout.items_provider, regionList).apply {
            binding.listRegions.setAdapter(this)
        }

    }
}