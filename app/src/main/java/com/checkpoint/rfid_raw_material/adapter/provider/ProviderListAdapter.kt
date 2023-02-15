package com.checkpoint.rfid_raw_material.adapter.provider

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.checkpoint.rfid_raw_material.R
import com.checkpoint.rfid_raw_material.adapter.inventory.InventoryListAdapter
import com.checkpoint.rfid_raw_material.model.InventoryItem
import com.checkpoint.rfid_raw_material.model.ProviderItem

class ProviderListAdapter(private val dataSet: List<ProviderItem>,
                          private val onClickListener: OnClickListener
):
    RecyclerView.Adapter<InventoryListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewItem: TextView

        init {
            textViewItem = view.findViewById(R.id.tvItemProvider)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): InventoryListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.provider_item_row, parent, false)
        return InventoryListAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: InventoryListAdapter.ViewHolder, position: Int) {
        holder.textViewItem.text = dataSet[position].name
        holder.itemView.setOnClickListener {
            onClickListener.onClick(dataSet[position])
        }
    }

    class OnClickListener(val clickListener: (provider: ProviderItem) -> Unit) {
        fun onClick(provider: ProviderItem) = clickListener(provider)
    }

    override fun getItemCount()= dataSet.size

}