package com.checkpoint.rfid_raw_material.ui.inventory.items

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.checkpoint.rfid_raw_material.R
import com.checkpoint.rfid_raw_material.adapter.TagsListAdapter
import com.checkpoint.rfid_raw_material.databinding.FragmentItemsReadBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val READ_NUMBER = "readNumber"
class ItemsReadFragment : Fragment() {
    private lateinit var viewModel: ItemsReadViewModel
    private  var _binding: FragmentItemsReadBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this)[ItemsReadViewModel::class.java]
        _binding = FragmentItemsReadBinding.inflate(inflater, container, false)

        val lvTags= binding.expandableTags.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.lvTags)
        lvTags.layoutManager= LinearLayoutManager(context)

        val expandableTags= binding.expandableTags
        expandableTags.parentLayout.setOnClickListener { expandableTags.toggleLayout() }


        var readNumber= viewModel.getReadNumber()
        viewModel?.getTagsList(readNumber)!!.observe(viewLifecycleOwner) {
                lvTags.adapter = TagsListAdapter(it)
        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = ItemsReadFragment().apply {

        }
    }

}