package com.checkpoint.rfid_raw_material.adapter

import android.bluetooth.BluetoothDevice
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.checkpoint.rfid_raw_material.R

class DevicesListAdapter(private val dataSet: List<String>, private val onClickListener: OnClickListener) :
    RecyclerView.Adapter<DevicesListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDevice: TextView

        init {
            tvDevice = view.findViewById(R.id.tvDevice)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DevicesListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_row_devices, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: DevicesListAdapter.ViewHolder, position: Int) {
        holder.tvDevice.text = dataSet[position]
        holder.itemView.setOnClickListener {
            holder.itemView.setBackgroundColor(Color.LTGRAY)
            notifyItemChanged(last_position)
            last_position=position
            onClickListener.onClick(dataSet[position])
        }


    }

    class OnClickListener(val clickListener: (bluetoothDevice: String) -> Unit) {
        fun onClick(bluetoothDevice: String) = clickListener(bluetoothDevice)
    }
    override fun getItemCount(): Int {
        return dataSet.size
    }

    companion object {
        var last_position = 0
    }

}