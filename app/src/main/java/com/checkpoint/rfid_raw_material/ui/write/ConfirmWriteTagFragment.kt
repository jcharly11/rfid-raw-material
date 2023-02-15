package com.checkpoint.rfid_raw_material.ui.write

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.checkpoint.rfid_raw_material.R

class ConfirmWriteTagFragment : Fragment() {

    companion object {
        fun newInstance() = ConfirmWriteTagFragment()
    }

    private lateinit var viewModel: ConfirmWriteTagViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_confirm_write_tag, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ConfirmWriteTagViewModel::class.java)
        // TODO: Use the ViewModel
    }

}