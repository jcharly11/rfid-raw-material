package com.checkpoint.rfid_raw_material.ui.inventory.items

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.checkpoint.rfid_raw_material.R
import com.checkpoint.rfid_raw_material.adapter.InventoryListAdapter
import com.checkpoint.rfid_raw_material.adapter.TagsListAdapter
import com.checkpoint.rfid_raw_material.databinding.FragmentItemsReadBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.exp

class ItemsReadFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = ItemsReadFragment().apply {
        }
    }

    private lateinit var viewModel: ItemsReadViewModel
    private  var _binding: FragmentItemsReadBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this)[ItemsReadViewModel::class.java]
        _binding = FragmentItemsReadBinding.inflate(inflater, container, false)

        val lvInventory= binding.expandableInventory.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.lvInventory)
        val lvTags= binding.expandableTags.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.lvTags)

        lvInventory.layoutManager= LinearLayoutManager(context)
        lvTags.layoutManager= LinearLayoutManager(context)


        val expandableInventory= binding.expandableInventory
        val expandableTags= binding.expandableTags

        expandableInventory.parentLayout.setOnClickListener { expandableInventory.toggleLayout() }
        expandableTags.parentLayout.setOnClickListener { expandableTags.toggleLayout() }

        CoroutineScope(Dispatchers.Main).launch {
            viewModel?.getInventoryList()!!.observe(viewLifecycleOwner) {
                lvInventory.adapter = InventoryListAdapter(it)
            }
            val tags = viewModel!!.getTagsList()
            val adapterTags = TagsListAdapter(tags)
            lvTags.adapter = adapterTags
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ItemsReadViewModel::class.java)
        // TODO: Use the ViewModel
    }

}