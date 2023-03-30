package com.checkpoint.rfid_raw_material.ui.inventory

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.checkpoint.rfid_raw_material.MainActivity
import com.checkpoint.rfid_raw_material.R
import com.checkpoint.rfid_raw_material.databinding.FragmentInventoryPagerBinding
import com.checkpoint.rfid_raw_material.utils.LogCreator
import com.google.android.material.tabs.TabLayoutMediator
import com.checkpoint.rfid_raw_material.utils.PagerAdapter
import com.checkpoint.rfid_raw_material.utils.dialogs.DialogWaitForHandHeld
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InventoryPagerFragment : Fragment() {

    private lateinit var viewModel: InventoryPagerViewModel
    private var _binding: FragmentInventoryPagerBinding? = null
    private val binding get() = _binding!!
     private lateinit var dialogWaitForHandHeld: DialogWaitForHandHeld
    private var activityMain: MainActivity? = null
    private var batteryLevel: Int? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[InventoryPagerViewModel::class.java]
        _binding = FragmentInventoryPagerBinding.inflate(inflater, container, false)
        activityMain = requireActivity() as MainActivity

        dialogWaitForHandHeld =  DialogWaitForHandHeld(this)




        val viewPager = binding.pager
        val tabLayout = binding.tabLayout
        val itemsTitle = arrayOf(
            resources.getString(R.string.tab_tittle_inventory),
            resources.getString(R.string.tab_tittle_read)
        )
        viewPager.adapter = PagerAdapter(this@InventoryPagerFragment)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = itemsTitle[position]
        }.attach()

        viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if(position==1){
                    activityMain!!.lyCreateLog!!.visibility = View.VISIBLE
                    activityMain!!.batteryView!!.visibility = View.GONE
                    activityMain!!.btnHandHeldGun!!.visibility = View.GONE



                }
                else{
                    activityMain!!.lyCreateLog!!.visibility = View.GONE
                    activityMain!!.batteryView!!.visibility = View.VISIBLE
                    activityMain!!.btnHandHeldGun!!.visibility = View.VISIBLE

                }
            }
        })

        viewModel.startHandHeld()
        val maxPower = arguments?.getInt("maxPower")
        val session = arguments?.getString("session")

        if (maxPower != null) {
            if (maxPower > 0) {

                viewModel.restartHandeldSetNewPower(maxPower, session!!)
                dialogWaitForHandHeld.show()
            }
        }



        activityMain!!.btnHandHeldGun!!.setOnClickListener {
            val transmitPowerLevelList = viewModel.getCapabilities()
            val currentPower = viewModel.currentPower()
            Log.e("power","$currentPower")

            val bundle = bundleOf(
                "batteryLevel" to batteryLevel,
                "needTag" to true,
                "transmitPowerLevelList" to transmitPowerLevelList,
                "currentPower" to currentPower,
            )
            findNavController().navigate(R.id.handHeldConfigFragment,bundle)
        }
        activityMain!!.btnCreateLog!!.setOnClickListener {
            var logCreator= LogCreator(requireContext())
            CoroutineScope(Dispatchers.Main).launch {
                var inventoryList= viewModel.getInventoryList()
                var tagList= viewModel.getTagsList()
                logCreator.createLog("read",inventoryList)
                Thread.sleep(1500)
                logCreator.createLog("write",tagList!!)
            }
        }


        viewModel.dialogVisible.observe(viewLifecycleOwner) {
            if (it) {
                dialogWaitForHandHeld.dismiss()
                activityMain!!.batteryView!!.visibility = View.VISIBLE
                activityMain!!.btnHandHeldGun!!.visibility = View.VISIBLE
                viewModel.callBatteryLevel()
            } else {
                dialogWaitForHandHeld.show()
            }
        }

        viewModel.percentCharge.observe(viewLifecycleOwner) {
            Log.e("Battery level: ", "$it")
            activityMain!!.batteryView!!.setPercent(it)
            batteryLevel = it

        }



        return binding.root
    }

    override fun onDetach() {
        super.onDetach()
        viewModel.destroy()
        Log.e("onDetach:  ", "$.....")


    }

    override fun onDestroyView() {
        super.onDestroyView()
      //  viewModel.destroy()

    }

}