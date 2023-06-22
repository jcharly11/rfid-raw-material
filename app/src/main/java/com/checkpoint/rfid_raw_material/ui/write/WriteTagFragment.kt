package com.checkpoint.rfid_raw_material.ui.write

import CustomDialogRemoveProvider
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.checkpoint.rfid_raw_material.ConfirmWriteActivity
import com.checkpoint.rfid_raw_material.MainActivity
import com.checkpoint.rfid_raw_material.R
import com.checkpoint.rfid_raw_material.databinding.FragmentWriteTagBinding
import com.checkpoint.rfid_raw_material.source.model.ProviderModel
import com.checkpoint.rfid_raw_material.utils.LogCreator
import com.checkpoint.rfid_raw_material.utils.dialogs.CustomDialogProvider
import com.checkpoint.rfid_raw_material.utils.dialogs.CustomDialogWriteTag
import com.checkpoint.rfid_raw_material.utils.dialogs.DialogBarcodeReaderStatus
import com.checkpoint.rfid_raw_material.utils.dialogs.DialogErrorEmptyFields
import com.checkpoint.rfid_raw_material.utils.dialogs.interfaces.CustomDialogProviderInterface
import com.checkpoint.rfid_raw_material.utils.dialogs.interfaces.CustomDialogRemoveProviderInterface
import com.checkpoint.rfid_raw_material.utils.interfaces.CustomDialogWriteTagInterface
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
        dialogWriteTag = CustomDialogWriteTag(this.requireContext())
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

            binding.tvIdentifier.setOnFocusChangeListener { view, b ->
                if(b){
                    activityMain!!.startBarCodeReadInstance()
                }

            }
            binding.tvIdentifier.requestFocus()
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

                        viewModel.calculateEPC(
                            versionValue,
                            subversionValue,
                            typeValue,
                            idProvider.toString(),
                            pieceValue
                        ).let {
                            activityMain!!.resetBarCode()
                            activityMain!!.stopReadedBarCode()
                            activityMain!!.deviceDisconnect()
                            val intent = Intent(context, ConfirmWriteActivity::class.java)
                            intent.putExtra("epc", it)
                            startForResult.launch(intent)

                        }


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
    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            binding.tvIdentifier.setText(String())


        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activityMain!!.batteryView!!.visibility = View.INVISIBLE
        activityMain!!.btnHandHeldGun!!.visibility = View.INVISIBLE
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