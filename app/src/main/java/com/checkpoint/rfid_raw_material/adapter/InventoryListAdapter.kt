package com.checkpoint.rfid_raw_material.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.checkpoint.rfid_raw_material.R
import com.checkpoint.rfid_raw_material.source.db.Inventory

class InventoryListAdapter(private val dataSet: List<Inventory>):
    RecyclerView.Adapter<InventoryListAdapter.ViewHolder>(){

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textInventoryItem: TextView

        init {
            textInventoryItem = view.findViewById(R.id.tvItemInventory)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): InventoryListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.inventory_item_row, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: InventoryListAdapter.ViewHolder, position: Int) {
        holder.textInventoryItem.text = dataSet[position].epc
    }

    override fun getItemCount() = dataSet.size

}