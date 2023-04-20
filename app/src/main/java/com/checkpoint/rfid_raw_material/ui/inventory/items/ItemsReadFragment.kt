package com.checkpoint.rfid_raw_material.ui.inventory.items

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
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
import com.checkpoint.rfid_raw_material.utils.Reverse
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this)[ItemsReadViewModel::class.java]
        _binding = FragmentItemsReadBinding.inflate(inflater, container, false)
        activityMain = requireActivity() as MainActivity


        val tvProviderEPC = binding.expandableProvider.findViewById<TextView>(R.id.tvProviderEPC)
        val lvTags =
            binding.expandableTags.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.lvTags)
        lvTags.layoutManager = LinearLayoutManager(context)

        val expandableTags = binding.expandableTags
        expandableTags.parentLayout.setOnClickListener { expandableTags.toggleLayout() }

        val expandableProvider = binding.expandableProvider
        expandableProvider.parentLayout.setOnClickListener { expandableProvider.toggleLayout() }

        var readNumber = viewModel.getReadNumber()



        viewModel?.getTagsList(readNumber)!!.observe(viewLifecycleOwner) {

            lvTags.adapter = TagsListAdapter(
                it,
                activityMain!!,
                listProviders,
                TagsListAdapter.OnClickListener {
                    if (!expandableProvider.isExpanded) {
                        expandableProvider.toggleLayout()
                    }
                    /*var reverse= Reverse()
            var hexValue= reverse.hexadecimalToBinaryString(it.epc)
            var supplier= reverse.getSupplier()
            tvProviderEPC.text= supplier*/
                })
        }


        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(list: List<Provider>) = ItemsReadFragment().apply {
            listProviders = list
        }
    }

}