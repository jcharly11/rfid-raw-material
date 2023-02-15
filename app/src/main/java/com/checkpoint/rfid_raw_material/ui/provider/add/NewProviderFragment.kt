package com.checkpoint.rfid_raw_material.ui.provider.add

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.checkpoint.rfid_raw_material.R

class NewProviderFragment : Fragment() {

    companion object {
        fun newInstance() = NewProviderFragment()
    }

    private lateinit var viewModel: NewProviderViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_new_provider, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(NewProviderViewModel::class.java)
        // TODO: Use the ViewModel
    }

}