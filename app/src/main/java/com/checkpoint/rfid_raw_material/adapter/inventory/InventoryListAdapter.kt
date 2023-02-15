package com.checkpoint.rfid_raw_material.adapter.inventory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.checkpoint.rfid_raw_material.R
import com.checkpoint.rfid_raw_material.model.InventoryItem

class InventoryListAdapter(private val dataSet: List<InventoryItem>,
                           private val onClickListener: OnClickListener):
    RecyclerView.Adapter<InventoryListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewItem: TextView

        init {
            textViewItem = view.findViewById(R.id.tvItemInventory)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.inventory_item_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewItem.text = dataSet[position].item
        holder.itemView.setOnClickListener {
            onClickListener.onClick(dataSet[position])
        }
    }

    class OnClickListener(val clickListener: (inventory: InventoryItem) -> Unit) {
        fun onClick(inventory: InventoryItem) = clickListener(inventory)
    }

    override fun getItemCount()= dataSet.size
}