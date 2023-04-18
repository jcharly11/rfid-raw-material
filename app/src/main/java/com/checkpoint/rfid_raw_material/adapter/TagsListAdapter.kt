package com.checkpoint.rfid_raw_material.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.checkpoint.rfid_raw_material.R
import com.checkpoint.rfid_raw_material.source.db.Tags

class TagsListAdapter(private val dataSet: List<Tags>, private val onClickListener: OnClickListener):
    RecyclerView.Adapter<TagsListAdapter.ViewHolder>(){

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textTagItem: TextView

        init {
            textTagItem = view.findViewById(R.id.tvItemTag)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TagsListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tag_item_row, parent, false)

        return TagsListAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: TagsListAdapter.ViewHolder, position: Int) {
        holder.textTagItem.text = dataSet[position].epc
        holder.itemView.setOnClickListener {
            onClickListener.onClick(dataSet[position])
        }
    }
    class OnClickListener(val clickListener: (tag: Tags) -> Unit) {
        fun onClick(tag: Tags) = clickListener(tag)
    }

    override fun getItemCount() = dataSet.size

}