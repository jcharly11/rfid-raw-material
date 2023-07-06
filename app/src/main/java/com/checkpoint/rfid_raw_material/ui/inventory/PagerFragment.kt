package com.checkpoint.rfid_raw_material.ui.inventory

 import android.os.Bundle
 import android.util.Log
 import android.view.LayoutInflater
 import android.view.MenuItem
 import android.view.View
 import android.view.ViewGroup
 import android.widget.Toolbar
 import androidx.appcompat.app.AppCompatActivity
 import androidx.fragment.app.Fragment
 import com.checkpoint.rfid_raw_material.R
 import com.checkpoint.rfid_raw_material.ReadActivity
 import com.checkpoint.rfid_raw_material.databinding.FragmentInventoryPagerBinding
 import com.checkpoint.rfid_raw_material.utils.PagerAdapter
 import com.checkpoint.rfid_raw_material.utils.dialogs.DialogConfiguringModeHandHeld
 import com.google.android.material.tabs.TabLayoutMediator


class PagerFragment : Fragment() {

     private lateinit var dialogConfiguringModeHandHeld: DialogConfiguringModeHandHeld
     private var _binding: FragmentInventoryPagerBinding? = null
     private val binding get() = _binding!!
     private var activityRed: ReadActivity? = null




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
         _binding = FragmentInventoryPagerBinding.inflate(inflater, container, false)

        activityRed = requireActivity() as ReadActivity
        activityRed!!.btnCreateLog= binding!!.appRawMaterials.imgCreateLog
        activityRed!!.lyCreateLog= binding!!.appRawMaterials.lyCreateLog
        activityRed!!.btnHandHeldGun= binding!!.appRawMaterials.imgHandHeldGun
        activityRed!!.batteryView = binding!!.appRawMaterials.batteryView

        activityRed!!.btnHandHeldGun!!.setOnClickListener {
            activityRed!!.handHeldConfig()
        }

          var readNumber = arguments?.getInt("readNumber")
        if(readNumber==null)
            readNumber= 0

        val viewPager = binding.pager
        val tabLayout = binding.tabLayout
        val itemsTitle = arrayOf(
            resources.getString(R.string.tab_tittle_inventory),
            resources.getString(R.string.tab_tittle_read)
        )




        viewPager.adapter = PagerAdapter(this@PagerFragment, readNumber)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = itemsTitle[position]
        }.attach()
        dialogConfiguringModeHandHeld = DialogConfiguringModeHandHeld(this@PagerFragment)


/*
        activityRed!!.btnCreateLog!!.visibility = View.VISIBLE
        activityRed!!.lyCreateLog!!.visibility = View.VISIBLE
        activityRed!!.batteryView!!.visibility = View.VISIBLE
        activityRed!!.btnHandHeldGun!!.visibility = View.VISIBLE

*/
        return binding.root
    }


}