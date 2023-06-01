package com.checkpoint.rfid_raw_material.ui.inventory.items

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.checkpoint.rfid_raw_material.MainActivity
import com.checkpoint.rfid_raw_material.R
import com.checkpoint.rfid_raw_material.adapter.TagsListAdapter
import com.checkpoint.rfid_raw_material.databinding.FragmentItemsReadBinding
import com.checkpoint.rfid_raw_material.source.db.Provider
import com.checkpoint.rfid_raw_material.source.db.Tags
import com.checkpoint.rfid_raw_material.utils.Reverse
import com.checkpoint.rfid_raw_material.utils.ReverseStandAlone
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val READ_NUMBER = "readNumber"

class ItemsReadFragment : Fragment() {
    private lateinit var viewModel: ItemsReadViewModel
    private var _binding: FragmentItemsReadBinding? = null
    private val binding get() = _binding!!
    private var activityMain: MainActivity? = null
    private var listProviders: List<Provider> = listOf()
    private var listTags: List<Tags> = listOf()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this)[ItemsReadViewModel::class.java]
        _binding = FragmentItemsReadBinding.inflate(inflater, container, false)
        activityMain = requireActivity() as MainActivity


        val tvProviderEPC = binding.expandableProvider.findViewById<TextView>(R.id.tvProviderEPC)
        val tvIdentifier = binding.expandableProvider.findViewById<TextView>(R.id.provider)

        val tvType = binding.expandableProvider.findViewById<TextView>(R.id.type)
        val tvVersion = binding.expandableProvider.findViewById<TextView>(R.id.version)
        val tvSubversion = binding.expandableProvider.findViewById<TextView>(R.id.subversion)

        val lvTags =
            binding.expandableTags.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.lvTags)
        lvTags.layoutManager = LinearLayoutManager(context)

        val expandableTags = binding.expandableTags
        expandableTags.parentLayout.setOnClickListener { expandableTags.toggleLayout() }

        val expandableProvider = binding.expandableProvider
        expandableProvider.parentLayout.setOnClickListener { expandableProvider.toggleLayout() }

        var readNumber = viewModel.getReadNumber()


        viewModel.getProvidersList().observe(viewLifecycleOwner){
           listProviders= it
            Log.e("getProvidersList","${it.size}")

            viewModel?.getTagsList(readNumber)!!.observe(viewLifecycleOwner) { it ->
                Log.e("getTagsList","${it.size}")

                listTags = it
                lvTags.adapter = TagsListAdapter(
                    listTags,
                    activityMain!!,
                    listProviders,
                    TagsListAdapter.OnClickListener {
                        if (!expandableProvider.isExpanded) {
                            expandableProvider.toggleLayout()
                        }

                        try {

                            var rever = ReverseStandAlone()
                            rever.hexadecimalToBinaryString(it.epc)
                            tvProviderEPC.text=  rever.getPiece()
                            tvIdentifier.text = rever.getSupplier()
                            tvType.text = rever.getType()
                            tvSubversion.text = rever.getSubVersion()
                            tvVersion.text = rever.getVersion()

                        }catch (ex: Exception){

                        }

                    })

            }
        }





        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = ItemsReadFragment().apply {
         }
    }

}