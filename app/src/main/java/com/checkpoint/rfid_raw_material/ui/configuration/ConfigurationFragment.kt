package com.checkpoint.rfid_raw_material.ui.configuration

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.checkpoint.rfid_raw_material.R
import com.checkpoint.rfid_raw_material.databinding.FragmentConfigurationBinding
import com.checkpoint.rfid_raw_material.source.model.LanguageModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class ConfigurationFragment : Fragment() {
    private lateinit var viewModel: ConfigurationViewModel
    private var _binding: FragmentConfigurationBinding? = null
    private val binding get() = _binding!!

    var selectedLanguage:String=""


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

                binding.spLanguageList.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            selectedLanguage = languageList[position].lang!!
                            var a = 0
                        }
                        override fun onNothingSelected(p0: AdapterView<*>?) {
                        }
                    }

             } else
                insertLanguage()
        }
    }

     private fun insertLanguage() {
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.insertLanguages()
            getLanguageList()
        }
    }


    fun changeLanguage(){
        if(selectedLanguage!="") {
            viewModel.setLanguage(selectedLanguage).apply {
                val config = resources.configuration
                val locale = Locale(selectedLanguage)
                Locale.setDefault(locale)
                config.locale = locale
                resources.updateConfiguration(config, resources.displayMetrics)
                requireActivity().recreate()
                findNavController().navigate(R.id.optionsWriteFragment)
            }
         }
        else
            Toast.makeText(context, "Please, selected language", Toast.LENGTH_SHORT).show()
    }

}