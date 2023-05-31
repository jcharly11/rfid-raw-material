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
        val tvIdentifier = binding.expandableProvider.findViewById<TextView>(R.id.identifier)
        val tvType = binding.expandableProvider.findViewById<TextView>(R.id.type)
        val tvSubtype = binding.expandableProvider.findViewById<TextView>(R.id.subtype)

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

            viewModel?.getTagsList(readNumber)!!.observe(viewLifecycleOwner) {
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
                        var rever = ReverseStandAlone()
                        var hexValue= rever.hexadecimalToBinaryString(it.epc)
                        var supplier= rever.getSupplier()
                        tvProviderEPC.text= supplier
                        tvType.text = rever.getType()
                        tvSubtype.text = rever.getSubVersion()


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