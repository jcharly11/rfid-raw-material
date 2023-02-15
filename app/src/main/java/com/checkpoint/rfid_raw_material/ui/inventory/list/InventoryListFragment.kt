package com.checkpoint.rfid_raw_material.ui.inventory.list

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.checkpoint.rfid_raw_material.R

class InventoryListFragment : Fragment() {

    companion object {
        fun newInstance() = InventoryListFragment()
    }

    private lateinit var viewModel: InventoryListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_inventory_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(InventoryListViewModel::class.java)
        // TODO: Use the ViewModel
    }

}