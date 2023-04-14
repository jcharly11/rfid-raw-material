package com.checkpoint.rfid_raw_material.ui.inventory

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.checkpoint.rfid_raw_material.MainActivity
import com.checkpoint.rfid_raw_material.R
import com.checkpoint.rfid_raw_material.databinding.FragmentInventoryPagerBinding
import com.checkpoint.rfid_raw_material.utils.LogCreator
import com.google.android.material.tabs.TabLayoutMediator
import com.checkpoint.rfid_raw_material.utils.PagerAdapter
import com.checkpoint.rfid_raw_material.utils.dialogs.DialogConfiguringModeHandHeld
import com.checkpoint.rfid_raw_material.utils.dialogs.DialogWaitForHandHeld
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PagerFragment : Fragment() {

    private lateinit var viewModel: PagerViewModel
    private lateinit var dialogConfiguringModeHandHeld: DialogConfiguringModeHandHeld
    private var _binding: FragmentInventoryPagerBinding? = null
    private val binding get() = _binding!!
    private var activityMain: MainActivity? = null
    private var batteryLevel: Int? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[PagerViewModel::class.java]
        _binding = FragmentInventoryPagerBinding.inflate(inflater, container, false)
        activityMain = requireActivity() as MainActivity
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

        val viewPager = binding.pager
        val tabLayout = binding.tabLayout
        val itemsTitle = arrayOf(
            resources.getString(R.string.tab_tittle_inventory),
            resources.getString(R.string.tab_tittle_read)
        )
        viewPager.adapter = PagerAdapter(this@PagerFragment,0)
        dialogConfiguringModeHandHeld= DialogConfiguringModeHandHeld(this@PagerFragment)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = itemsTitle[position]
        }.attach()



        activityMain!!.btnHandHeldGun!!.setOnClickListener {
        //    val readNumber= viewModel.getReadNumber()
            //todo implemnet config

        }

        lifecycleScope.launch{


            activityMain!!.lyCreateLog!!.visibility = View.VISIBLE
            activityMain!!.batteryView!!.visibility = View.VISIBLE
            activityMain!!.btnHandHeldGun!!.visibility = View.VISIBLE
        }

        return binding.root
    }

}