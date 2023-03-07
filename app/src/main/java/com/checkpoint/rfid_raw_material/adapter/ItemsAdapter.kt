package com.checkpoint.rfid_raw_material.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.checkpoint.rfid_raw_material.R
import com.checkpoint.rfid_raw_material.source.db.tblItem

class ItemsAdapter(private val dataSet: List<tblItem>) :
    RecyclerView.Adapter<ItemsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textItem: TextView

        init {
            textItem = view.findViewById(R.id.tvItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemsAdapter.ViewHolder, position: Int) {
        holder.textItem.text = dataSet[position].nameItem
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

}