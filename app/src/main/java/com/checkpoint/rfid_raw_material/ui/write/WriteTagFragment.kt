package com.checkpoint.rfid_raw_material.ui.write

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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.checkpoint.rfid_raw_material.MainActivity
import com.checkpoint.rfid_raw_material.R
import com.checkpoint.rfid_raw_material.databinding.FragmentWriteTagBinding
import com.checkpoint.rfid_raw_material.source.model.ProviderModel
import com.checkpoint.rfid_raw_material.utils.Conversor
import com.checkpoint.rfid_raw_material.utils.dialogs.CustomDialogProvider
import com.checkpoint.rfid_raw_material.utils.dialogs.DialogBarcodeReaderStatus
import com.checkpoint.rfid_raw_material.utils.dialogs.DialogErrorDeviceConnected
import com.checkpoint.rfid_raw_material.utils.dialogs.DialogErrorEmptyFields
import com.checkpoint.rfid_raw_material.utils.dialogs.interfaces.CustomDialogProviderInterface
import com.checkpoint.rfid_raw_material.utils.dialogs.CustomDialogWriteTag
import com.checkpoint.rfid_raw_material.utils.interfaces.CustomDialogWriteTagInterface
import com.checkpoint.rfid_raw_material.utils.LogCreator
import kotlinx.coroutines.*

class WriteTagFragment : Fragment(),
    CustomDialogProviderInterface, CustomDialogWriteTagInterface {
    private lateinit var viewModel: WriteTagViewModel
    private lateinit var dialogProvider: CustomDialogProvider
    private lateinit var dialogBarcodeReaderStatus: DialogBarcodeReaderStatus
    private lateinit var dialogErrorDeviceConnected: DialogErrorDeviceConnected
    private lateinit var dialogErrorEmptyFields: DialogErrorEmptyFields
    private lateinit var dialogWriteTag: CustomDialogWriteTag

    private var _binding: FragmentWriteTagBinding? = null
    private val binding get() = _binding!!

    var idProvider: Int = 0
    private var deviceStarted = false
    private var activityMain: MainActivity? = null
    private var readNumber: Int? = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[WriteTagViewModel::class.java]
        _binding = FragmentWriteTagBinding.inflate(inflater, container, false)
        activityMain = requireActivity() as MainActivity

        dialogProvider = CustomDialogProvider(this@WriteTagFragment)
        dialogBarcodeReaderStatus = DialogBarcodeReaderStatus(this@WriteTagFragment)
        dialogErrorDeviceConnected = DialogErrorDeviceConnected(this@WriteTagFragment)
        dialogErrorEmptyFields = DialogErrorEmptyFields(this@WriteTagFragment)
        dialogWriteTag = CustomDialogWriteTag(this@WriteTagFragment)

        activityMain!!.lyCreateLog!!.visibility = View.VISIBLE
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {}

        readNumber = arguments?.getInt("readNumber")
        CoroutineScope(Dispatchers.Main).launch {
            if (readNumber == null) {
                readNumber = viewModel.getNewReadNumber()
            }
        }

        getProviderList()
        viewModel.liveCode.observe(viewLifecycleOwner) {
            binding.tvIdentifier.setText(it)
        }


       /*
        binding.tvIdentifier.setOnFocusChangeListener { _, b ->
            Log.e("setOnFocusChangeListener", "$b")
            if (b) {
                deviceStarted = true
                dialogBarcodeReaderStatus.show()
                this.lifecycleScope.launch {
                    viewModel.startHandHeldBarCode()
                }
            }
        }
        */


        viewModel.deviceConnected.observe(viewLifecycleOwner) {
            dialogBarcodeReaderStatus.dismiss()

            if (!it && deviceStarted) {
                dialogErrorDeviceConnected.show()
                deviceStarted = false
            }
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
                        typeValue.isNotEmpty() && pieceValue.isNotEmpty()
                    ) {

                        val conversor = Conversor()
                        var hexValueEpc = ""
                        val version = conversor.toBinaryString(versionValue, 5, '0')
                        val subVersion = conversor.toBinaryString(subversionValue, 5, '0')
                        val type = conversor.toBinaryString(typeValue, 6, '0')
                        val supplier = conversor.toBinaryString(idProvider.toString(), 32, '0')
                        val piece = conversor.toBinaryString(pieceValue, 80, '0')


                        val binaryChain = "$version$type$subVersion$piece$supplier"
                        val binaryGroup = binaryChain.chunked(4)
                        binaryGroup.iterator().forEach {
                            hexValueEpc += conversor.toHexadecimalString(it)
                        }


                        viewModel.newTag(
                            readNumber!!,
                            versionValue,
                            subversionValue,
                            typeValue,
                            pieceValue,
                            idProvider,
                            hexValueEpc
                        )
                        val bundle = bundleOf(
                            "epc" to hexValueEpc,
                            "readNumber" to readNumber
                        )
                        lifecycleScope.launch {
                            viewModel.disconnectDevice()
                        }
                        findNavController().navigate(R.id.confirmWriteTagFragment, bundle)

                    } else {

                        dialogErrorEmptyFields.show()
                    }

                }

            } catch (ex: Exception) {
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
            var a = readNumber
            dialogWriteTag.show()
        }


        return binding.root
    }

    private fun getProviderList() {
        CoroutineScope(Dispatchers.Main).launch {
            val providerList = viewModel.getProviderList()
            val adapter: ArrayAdapter<ProviderModel> =
                ArrayAdapter<ProviderModel>(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    providerList
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
                        idProvider = providerList[position].id

                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                    }
                }
            closeDialog()
        }
    }

    private fun insertProviders() {
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.insertInitialProviders()
            getProviderList()
        }
    }

    override fun saveProvider() {
        val idProvider = dialogProvider.tvIdProvider!!.text.toString()
        val idASProvider = dialogProvider.tvIdASProvider!!.text.toString()
        val nameProvider = dialogProvider.tvNameProvider!!.text.toString()

        if (idProvider.isNotEmpty() && idASProvider.isNotEmpty() && nameProvider.isNotEmpty()) {
            CoroutineScope(Dispatchers.Main).launch {
                viewModel.newProvider(idProvider.toInt(), idASProvider, nameProvider)
            }
            Thread.sleep(1000)
            getProviderList()
            binding.spProviderList.refreshDrawableState()
        } else
            Toast.makeText(
                context,
                requireContext().resources.getString(R.string.check_fields), Toast.LENGTH_SHORT
            ).show()
    }

    override fun finishWrite() {
        dialogWriteTag.dismiss()
        findNavController().navigate(R.id.optionsWriteFragment)
    }

    override fun closeDialogWrite() {
        dialogWriteTag.dismiss()
    }

    override fun closeDialog() {
        dialogProvider.dismiss()
    }

    override fun onStart() {
        super.onStart()
        deviceStarted = true
        dialogBarcodeReaderStatus.show()
        this.lifecycleScope.launch {
            viewModel.startHandHeldBarCode()
        }
    }
}