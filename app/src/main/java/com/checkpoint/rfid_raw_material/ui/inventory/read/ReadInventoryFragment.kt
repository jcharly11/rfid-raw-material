package com.checkpoint.rfid_raw_material.ui.inventory.read

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.checkpoint.rfid_raw_material.R
import com.checkpoint.rfid_raw_material.databinding.FragmentReadInventoryBinding
import com.checkpoint.rfid_raw_material.enums.TypeInventory
import com.checkpoint.rfid_raw_material.source.db.Inventory
import com.checkpoint.rfid_raw_material.utils.dialogs.CustomDialogInventory
import com.checkpoint.rfid_raw_material.utils.interfaces.CustomDialogInventoryInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class ReadInventoryFragment : Fragment(),CustomDialogInventoryInterface {

    companion object {
        @JvmStatic
        fun newInstance() = ReadInventoryFragment().apply {

        }
    }
    private lateinit var viewModel: ReadInventoryViewModel
    private  var _binding: FragmentReadInventoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var dialog: CustomDialogInventory


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        viewModel = ViewModelProvider(this)[ReadInventoryViewModel::class.java]
        _binding = FragmentReadInventoryBinding.inflate(inflater, container, false)

        CoroutineScope(Dispatchers.Main).launch {
            binding.tvTagsWrited.text = "${viewModel?.getTagsList()!!.size}"

            viewModel?.counterTags()!!.observe(viewLifecycleOwner){
                binding.tvTagsWrited.text = "${it!!.size}"

            }


            viewModel?.getInventoryList()!!.observe(viewLifecycleOwner) {
                binding.tvItemsScanned.text = "${it.size}"
            }
        }

        binding.btnStart.setOnClickListener {
            dialog = CustomDialogInventory(this@ReadInventoryFragment, TypeInventory.START_INVENTORY)
            dialog.show()
        }

        binding.btnPause.setOnClickListener {
            dialog = CustomDialogInventory(this@ReadInventoryFragment,TypeInventory.PAUSE_INVENTORY)
            dialog.show()
        }

        binding.btnFinishInventory.setOnClickListener {
            dialog = CustomDialogInventory(this@ReadInventoryFragment,TypeInventory.FINISH_INVENTORY)
            dialog.show()
        }
        return binding.root
    }


    override fun startInventory() {
        binding.btnStart.visibility = View.INVISIBLE
        binding.btnPause.visibility = View.VISIBLE
        viewModel!!.pauseInventory(false)


        closeDialog()
    }

    override fun pauseInventory() {
        binding.btnPause.visibility = View.INVISIBLE
        binding.btnStart.visibility = View.VISIBLE
        viewModel!!.pauseInventory(true)
        closeDialog()
    }

    override fun finishInventory() {
        closeDialog()
        findNavController().navigate(R.id.optionsWriteFragment)
    }

    override fun closeDialog() {
        dialog.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.disconnectDevice()
    }

}

