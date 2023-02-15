package com.checkpoint.rfid_raw_material.ui.provider.list

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.checkpoint.rfid_raw_material.R

class ProviderListFragment : Fragment() {

    companion object {
        fun newInstance() = ProviderListFragment()
    }

    private lateinit var viewModel: ProviderListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_provider_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProviderListViewModel::class.java)
        // TODO: Use the ViewModel
    }

}