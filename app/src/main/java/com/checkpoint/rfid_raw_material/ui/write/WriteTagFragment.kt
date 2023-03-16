package com.checkpoint.rfid_raw_material.ui.write

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.checkpoint.rfid_raw_material.R
import com.checkpoint.rfid_raw_material.databinding.FragmentWriteTagBinding
import com.checkpoint.rfid_raw_material.source.model.ProviderModel
import com.checkpoint.rfid_raw_material.utils.Conversor
import com.checkpoint.rfid_raw_material.utils.dialogs.CustomDialogProvider
import com.checkpoint.rfid_raw_material.utils.interfaces.CustomDialogProviderInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonDisposableHandle.parent
import kotlinx.coroutines.launch
import java.util.*

class WriteTagFragment : Fragment(),
    CustomDialogProviderInterface{
    private lateinit var viewModel: WriteTagViewModel
    private lateinit var dialogProvider: CustomDialogProvider

    private var _binding: FragmentWriteTagBinding? = null
    private val binding get() = _binding!!

    var idProvider:Int=0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this)[WriteTagViewModel::class.java]
        _binding = FragmentWriteTagBinding.inflate(inflater, container, false)

        dialogProvider = CustomDialogProvider(this@WriteTagFragment)

        getProviderList()

        binding.btnWriteTag.setOnClickListener {
            try {
                CoroutineScope(Dispatchers.Main).launch {
                    val versionValue= binding.tvVersion.text.toString()
                    val subversionValue= binding.tvSubversion.text.toString()
                    val typeValue= binding.tvType.text.toString()
                    val pieceValue= binding.tvIdentifier.text.toString()

                    val conversor =  Conversor()
                    var hexValueEpc = ""
                    var version = conversor.toBinaryString(versionValue,5,'0')
                    var subVersion = conversor.toBinaryString(subversionValue,5,'0')
                    var type = conversor.toBinaryString(typeValue,6,'0')
                    var supplier = conversor.toBinaryString(idProvider.toString(),32,'0')
                    var piece = conversor.toBinaryString(pieceValue,80,'0')

                    var binaryChain = "$version$type$subVersion$piece$supplier"
                    var binaryGroup = binaryChain.chunked(4)
                    binaryGroup.iterator().forEach {
                        hexValueEpc += conversor.toHexadecimalString(it)
                    }


                    var newTag= viewModel.newTag(versionValue,subversionValue,typeValue,pieceValue,idProvider,hexValueEpc)

                    Toast.makeText(context, "$hexValueEpc", Toast.LENGTH_SHORT).show()
                }
            }
            catch (ex:Exception){
                Log.e("logError",ex.toString())
            }
        }

        binding.btnAddProvider.setOnClickListener {
            dialogProvider.show()
        }


        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(WriteTagViewModel::class.java)
        // TODO: Use the ViewModel
    }

    fun getProviderList(){
        CoroutineScope(Dispatchers.Main).launch {
            val providerList= viewModel.getProviderList()

            if(providerList.size>0) {
                val adapter: ArrayAdapter<ProviderModel> =
                    ArrayAdapter<ProviderModel>(
                        requireContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        providerList
                    )
                binding.spProviderList.setAdapter(adapter)

                binding.spProviderList.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            idProvider = providerList[position].id
                            var a = 0
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {
                        }
                    }
            }
            else
                insertProviders()

            closeDialog()
        }
    }

    fun insertProviders(){
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.insertInitialProviders()
            getProviderList()
        }
    }
    override fun saveProvider() {
        val idProvider= dialogProvider.tvIdProvider!!.text.toString()
        val idASProvider= dialogProvider.tvIdASProvider!!.text.toString()
        val nameProvider= dialogProvider.tvNameProvider!!.text.toString()

        if(!idProvider.isNullOrEmpty() && !idASProvider.isNullOrEmpty() && !nameProvider.isNullOrEmpty()){
            var newProvider = CoroutineScope(Dispatchers.Main).launch {
                viewModel.newProvider(idProvider.toInt(),idASProvider,nameProvider)
            }
            Thread.sleep(1000)
            getProviderList()
            binding.spProviderList.refreshDrawableState()
        }
        else
            Toast.makeText(context, "${requireContext()!!.resources.getString(R.string.check_fields)}", Toast.LENGTH_SHORT).show()
    }

    override fun closeDialog() {
        dialogProvider.dismiss()
    }

}