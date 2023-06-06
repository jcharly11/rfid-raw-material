package com.checkpoint.rfid_raw_material.ui.write

import CustomDialogRemoveProvider
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.checkpoint.rfid_raw_material.MainActivity
import com.checkpoint.rfid_raw_material.R
import com.checkpoint.rfid_raw_material.databinding.FragmentWriteTagBinding
import com.checkpoint.rfid_raw_material.source.model.ProviderModel
import com.checkpoint.rfid_raw_material.utils.dialogs.CustomDialogProvider
import com.checkpoint.rfid_raw_material.utils.dialogs.DialogBarcodeReaderStatus
import com.checkpoint.rfid_raw_material.utils.dialogs.DialogErrorDeviceConnected
import com.checkpoint.rfid_raw_material.utils.dialogs.DialogErrorEmptyFields
import com.checkpoint.rfid_raw_material.utils.dialogs.interfaces.CustomDialogProviderInterface
import com.checkpoint.rfid_raw_material.utils.dialogs.CustomDialogWriteTag
import com.checkpoint.rfid_raw_material.utils.interfaces.CustomDialogWriteTagInterface
import com.checkpoint.rfid_raw_material.utils.LogCreator
import com.checkpoint.rfid_raw_material.utils.dialogs.interfaces.CustomDialogRemoveProviderInterface
import io.sentry.Sentry
import kotlinx.coroutines.*

class WriteTagFragment : Fragment(),
    CustomDialogProviderInterface, CustomDialogWriteTagInterface,
    CustomDialogRemoveProviderInterface {
    private lateinit var viewModel: WriteTagViewModel
    private lateinit var dialogProvider: CustomDialogProvider
    private lateinit var dialogBarcodeReaderStatus: DialogBarcodeReaderStatus
    private lateinit var dialogErrorEmptyFields: DialogErrorEmptyFields
    private lateinit var dialogWriteTag: CustomDialogWriteTag
    private lateinit var dialogRemoveProvider: CustomDialogRemoveProvider

    private var _binding: FragmentWriteTagBinding? = null
    private val binding get() = _binding!!

    var idProvider: Int = 0
    var idSupplier = String()


    private var activityMain: MainActivity? = null
    private var readNumber: Int? = 0
    private var deviceName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[WriteTagViewModel::class.java]
        _binding = FragmentWriteTagBinding.inflate(inflater, container, false)
        activityMain = requireActivity() as MainActivity

        dialogProvider = CustomDialogProvider(this@WriteTagFragment)

        dialogBarcodeReaderStatus = DialogBarcodeReaderStatus(this@WriteTagFragment)
        dialogErrorEmptyFields = DialogErrorEmptyFields(this@WriteTagFragment)
        dialogWriteTag = CustomDialogWriteTag(this@WriteTagFragment)
        dialogRemoveProvider = CustomDialogRemoveProvider(this@WriteTagFragment)




        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {}

        readNumber = arguments?.getInt("readNumber")
        deviceName = arguments?.getString("deviceName")

        CoroutineScope(Dispatchers.Main).launch {
            if (readNumber == null || readNumber == 0) {
                readNumber = viewModel.getNewReadNumber()
            }
        }

        activityMain!!.liveCode.observe(viewLifecycleOwner) {
            binding.tvIdentifier.setText(it.trim())
        }


        viewModel.getProvidersList().observe(viewLifecycleOwner) {
            var listProviders: MutableList<ProviderModel> = mutableListOf()


            it.iterator().forEachRemaining {
                listProviders.add(ProviderModel(it.id, it.idAS, it.name))
            }

            if (listProviders.size == 0)
                binding.btnRemoveProvider.visibility = View.GONE
            else
                binding.btnRemoveProvider.visibility = View.VISIBLE

            val adapter: ArrayAdapter<ProviderModel> =
                ArrayAdapter<ProviderModel>(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    listProviders
                )

            binding.spProviderList.adapter = adapter
            binding.spProviderList.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        idProvider = listProviders[position].id
                        idSupplier = listProviders[position].idAS!!
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                    }
                }
        }
        binding.btnRemoveProvider.setOnClickListener {
            dialogRemoveProvider.show()
        }
        binding.btnWriteTag.setOnClickListener {
            try {
                CoroutineScope(Dispatchers.Main).launch {
                    val versionValue = binding.tvVersion.text.toString()
                    val subversionValue = binding.tvSubversion.text.toString()
                    val typeValue = binding.tvType.text.toString()
                    val pieceValue = binding.tvIdentifier.text.toString()

                    if (versionValue.isNotEmpty() &&
                        subversionValue.isNotEmpty() &&
                        typeValue.isNotEmpty() && pieceValue.isNotEmpty() &&
                        idProvider > 0
                    ) {

                        val hexValueEpc = viewModel.calculateEPC(
                            versionValue,
                            subversionValue,
                            typeValue,
                            idProvider.toString(),
                            pieceValue
                        )

                        val bundle = bundleOf(
                            "epc" to hexValueEpc,
                            "readNumber" to readNumber,
                            "deviceName" to deviceName,
                            "version" to versionValue,
                            "subversion" to subversionValue,
                            "type" to typeValue,
                            "identifier" to pieceValue,
                            "provider" to idProvider
                        )
                        activityMain!!.resetBarCode()
                        findNavController().navigate(R.id.confirmWriteTagFragment, bundle)

                    } else {

                        dialogErrorEmptyFields.show()
                    }

                }

            } catch (ex: Exception) {
                Sentry.captureMessage("${ex.message}")
                Log.e("logError", ex.toString())
            }
        }

        binding.btnAddProvider.setOnClickListener {
            dialogProvider.show()
        }

        activityMain!!.btnCreateLog!!.setOnClickListener {
            var logCreator = LogCreator(requireContext())
            CoroutineScope(Dispatchers.Main).launch {
                var tagList = viewModel.getTagsForLog(readNumber!!)
                logCreator.createLog("write", tagList!!)
            }
        }

        binding.btnFinishWrite.setOnClickListener {
            dialogWriteTag.show()
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activityMain!!.startBarCodeReadInstance()
        activityMain!!.btnCreateLog!!.visibility = View.VISIBLE
        activityMain!!.lyCreateLog!!.visibility = View.VISIBLE
        activityMain!!.batteryView!!.visibility = View.VISIBLE
        activityMain!!.btnHandHeldGun!!.visibility = View.VISIBLE
    }


    override fun saveProvider() {
        val idProvider = dialogProvider.tvIdProvider!!.text.toString()
        val idASProvider = dialogProvider.tvIdASProvider!!.text.toString()
        val nameProvider = dialogProvider.tvNameProvider!!.text.toString()

        if (idProvider.isNotEmpty() && idASProvider.isNotEmpty() && nameProvider.isNotEmpty()) {
            CoroutineScope(Dispatchers.Main).launch {
                viewModel.newProvider(idProvider.toInt(), idASProvider, nameProvider)
            }
            binding.btnRemoveProvider.visibility = View.VISIBLE
            closeDialog()
        } else
            Toast.makeText(
                context,
                requireContext().resources.getString(R.string.check_fields), Toast.LENGTH_SHORT
            ).show()
    }

    override fun finishWrite() {
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.saveReadNumber(0)
            dialogWriteTag.dismiss()
            findNavController().navigate(R.id.optionsWriteFragment)
        }

    }

    override fun closeDialogWrite() {
        dialogWriteTag.dismiss()
    }

    override fun closeDialog() {
        dialogProvider.dismiss()
    }


    override fun closeDialogRemoveProvider() {
        dialogRemoveProvider.dismiss()
    }

    override fun removeProvider() {
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.deleteProvider(idProvider)
            dialogRemoveProvider.dismiss()
        }
    }


}