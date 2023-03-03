package com.checkpoint.rfid_raw_material.ui.selection

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.navigation.fragment.findNavController
import com.checkpoint.rfid_raw_material.MainActivity
import com.checkpoint.rfid_raw_material.R
import com.checkpoint.rfid_raw_material.databinding.FragmentOptionsWriteBinding
import com.checkpoint.rfid_raw_material.source.model.Item
import com.checkpoint.rfid_raw_material.utils.CustomBattery
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class OptionsWriteFragment : Fragment() {

    private lateinit var viewModel: OptionsWriteViewModel
    private var _binding: FragmentOptionsWriteBinding? = null
    private val binding get() = _binding!!
    private var activityMain: MainActivity? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProvider(this)[OptionsWriteViewModel::class.java]
        _binding = FragmentOptionsWriteBinding.inflate(inflater, container, false)
        activityMain = requireActivity() as MainActivity

        binding.btnInventory.setOnClickListener {
            findNavController().navigate(R.id.inventoryPagerFragment)
        }
        binding.btnWriteTag.setOnClickListener {
            findNavController().navigate(R.id.writeTagFragment)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        activityMain!!.batteryView!!.visibility = View.GONE
        activityMain!!.btnHandHeldGun!!.visibility = View.GONE
        activityMain!!.lyCreateLog!!.visibility = View.GONE
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(OptionsWriteViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).supportActionBar!!.show()
    }
}