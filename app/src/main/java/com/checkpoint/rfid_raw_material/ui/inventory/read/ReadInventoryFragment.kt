package com.checkpoint.rfid_raw_material.ui.inventory.read

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.checkpoint.rfid_raw_material.R

class ReadInventoryFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = ReadInventoryFragment().apply {

        }
    }
    private lateinit var viewModel: ReadInventoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_read_inventory, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ReadInventoryViewModel::class.java)
        // TODO: Use the ViewModel
    }

}