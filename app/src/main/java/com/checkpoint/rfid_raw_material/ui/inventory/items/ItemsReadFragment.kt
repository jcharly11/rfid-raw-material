package com.checkpoint.rfid_raw_material.ui.inventory.items

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.checkpoint.rfid_raw_material.R

class ItemsReadFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = ItemsReadFragment().apply {
        }
    }

    private lateinit var viewModel: ItemsReadViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_items_read, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ItemsReadViewModel::class.java)
        // TODO: Use the ViewModel
    }

}