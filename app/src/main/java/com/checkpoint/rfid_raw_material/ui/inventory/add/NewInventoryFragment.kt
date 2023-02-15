package com.checkpoint.rfid_raw_material.ui.inventory.add

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.checkpoint.rfid_raw_material.R

class NewInventoryFragment : Fragment() {

    companion object {
        fun newInstance() = NewInventoryFragment()
    }

    private lateinit var viewModel: NewInventoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_new_inventory, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(NewInventoryViewModel::class.java)
        // TODO: Use the ViewModel
    }

}