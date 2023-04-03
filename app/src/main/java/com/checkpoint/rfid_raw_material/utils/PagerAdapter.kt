package com.checkpoint.rfid_raw_material.utils

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.checkpoint.rfid_raw_material.ui.inventory.items.ItemsReadFragment
import com.checkpoint.rfid_raw_material.ui.inventory.read.ReadInventoryFragment

class PagerAdapter(fragment: Fragment,private  val readNumb: Int): FragmentStateAdapter(fragment){
    val readFragment = ReadInventoryFragment.newInstance(readNumb)
    val listFragment = ItemsReadFragment.newInstance()
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        when(position){
            0 -> return readFragment
            1 -> return listFragment
        }
        return ReadInventoryFragment.newInstance(readNumb)
    }
}