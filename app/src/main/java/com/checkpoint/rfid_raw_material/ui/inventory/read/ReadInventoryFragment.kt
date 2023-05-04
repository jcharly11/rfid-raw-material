package com.checkpoint.rfid_raw_material.ui.inventory.read

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.checkpoint.rfid_raw_material.MainActivity
import com.checkpoint.rfid_raw_material.R
import com.checkpoint.rfid_raw_material.databinding.FragmentReadInventoryBinding
import com.checkpoint.rfid_raw_material.enums.TypeInventory
import com.checkpoint.rfid_raw_material.utils.LogCreator
import com.checkpoint.rfid_raw_material.utils.dialogs.CustomDialogInventory
import com.checkpoint.rfid_raw_material.utils.dialogs.interfaces.CustomDialogInventoryInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val READ_NUMBER = "readNumber"

class ReadInventoryFragment : Fragment(), CustomDialogInventoryInterface {

    private lateinit var viewModel: ReadInventoryViewModel
    private var _binding: FragmentReadInventoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var dialog: CustomDialogInventory
    private var readNumber: Int? = 0
    private var activityMain: MainActivity? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
          readNumber = arguments?.getInt("readNumber")

        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        viewModel = ViewModelProvider(this)[ReadInventoryViewModel::class.java]
         _binding = FragmentReadInventoryBinding.inflate(inflater, container, false)
        activityMain = requireActivity() as MainActivity



        binding.btnStart.setOnClickListener {
            dialog =
                CustomDialogInventory(this@ReadInventoryFragment, TypeInventory.START_INVENTORY)
            dialog.show()


        }

        binding.btnPause.setOnClickListener {
            dialog =
                CustomDialogInventory(this@ReadInventoryFragment, TypeInventory.PAUSE_INVENTORY)
            dialog.show()
        }

        binding.btnFinishInventory.setOnClickListener {
            dialog =
                CustomDialogInventory(this@ReadInventoryFragment, TypeInventory.FINISH_INVENTORY)
            dialog.show()
        }

        activityMain!!.btnCreateLog!!.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                var listScanned = viewModel.getTagsList(readNumber!!)
                if (listScanned.size > 0) {
                    var logCreator = LogCreator(requireContext())

                    var tagList = viewModel.getTagsForLog(readNumber!!)
                    logCreator.createLog("read", tagList)
                } else {
                    Toast.makeText(
                        context,
                        resources.getText(R.string.error_log),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        CoroutineScope(Dispatchers.Main).launch {
            if(readNumber==0)
                readNumber = viewModel.getNewReadNumber()


            Log.e("CoroutineScope","$readNumber")
            viewModel?.getTagsListLive(readNumber!!)!!.observe(viewLifecycleOwner) {
                binding.tvItemsScanned.text = "${it.size}"
            }
        }


        return binding.root
    }


    companion object {
        @JvmStatic
        fun newInstance(readNumb:Int) = ReadInventoryFragment().apply {
            arguments = Bundle().apply {
                putInt(READ_NUMBER, readNumb)
            }
        }
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
        CoroutineScope(Dispatchers.Main).launch {
            val dataEmpty = viewModel.deleteCapturedData()
            if(dataEmpty){
                viewModel!!.saveReadNumber(0)
                activityMain!!.lyCreateLog!!.visibility = View.INVISIBLE
                activityMain!!.batteryView!!.visibility = View.INVISIBLE
                activityMain!!.btnHandHeldGun!!.visibility = View.INVISIBLE
                findNavController().navigate(R.id.optionsWriteFragment)
            }
        }

    }

    override fun closeDialog() {
        dialog.dismiss()

    }


}

