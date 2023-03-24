package com.checkpoint.rfid_raw_material.ui.configuration

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import com.checkpoint.rfid_raw_material.R
import com.checkpoint.rfid_raw_material.databinding.FragmentConfigurationBinding
import com.checkpoint.rfid_raw_material.databinding.FragmentWriteTagBinding
import com.checkpoint.rfid_raw_material.source.model.LanguageModel
import com.checkpoint.rfid_raw_material.source.model.ProviderModel
import com.checkpoint.rfid_raw_material.ui.write.WriteTagViewModel
import com.checkpoint.rfid_raw_material.utils.dialogs.CustomDialogProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class ConfigurationFragment : Fragment() {
    private lateinit var viewModel: ConfigurationViewModel
    private var _binding: FragmentConfigurationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[ConfigurationViewModel::class.java]
        _binding = FragmentConfigurationBinding.inflate(inflater, container, false)

        getLanguageList()

        binding.btnSaveConfiguration.setOnClickListener {
            changeLanguage()
        }

        return binding.root
    }

    private fun getLanguageList() {
        CoroutineScope(Dispatchers.Main).launch {
            val languageList = viewModel.getLanguageList()
            if (languageList.size > 0) {
                val adapter: ArrayAdapter<LanguageModel> =
                    ArrayAdapter<LanguageModel>(
                        requireContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        languageList
                    )
                binding.spLanguageList.adapter = adapter
            } else
                insertLanguage()
        }
    }

    private fun insertLanguage() {
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.insertLanguage()
            getLanguageList()
        }
    }

    private fun changeLanguage(){
        val lang:String= binding.spLanguageList.selectedItem.toString()
        viewModel.setLanguage(lang).apply {
            val config = resources.configuration
            val locale = Locale(lang)
            Locale.setDefault(locale)
            config.locale = locale
            resources.updateConfiguration(config, resources.displayMetrics)
            requireActivity().recreate()
            findNavController().navigate(R.id.optionsWriteFragment)
        }
    }

}