package com.checkpoint.rfid_raw_material.ui.inventory.list

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.checkpoint.rfid_raw_material.R
import com.checkpoint.rfid_raw_material.databinding.FragmentInventoryListBinding
import com.checkpoint.rfid_raw_material.databinding.FragmentOptionsWriteBinding
import com.checkpoint.rfid_raw_material.ui.selection.OptionsWriteViewModel

class InventoryListFragment : Fragment() {

    private lateinit var viewModel: InventoryListViewModel
    private var _binding: FragmentInventoryListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        viewModel = ViewModelProvider(this)[InventoryListViewModel::class.java]
        _binding = FragmentInventoryListBinding.inflate(inflater, container, false)

        binding.btnStartInventory.setOnClickListener {
            findNavController().navigate(R.id.inventoryPagerFragment)
        }
        return  binding.root

    }

}