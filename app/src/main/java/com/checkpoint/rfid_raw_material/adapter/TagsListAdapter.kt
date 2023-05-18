package com.checkpoint.rfid_raw_material.adapter
import android.app.Application
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.checkpoint.rfid_raw_material.MainActivity
import com.checkpoint.rfid_raw_material.R
import com.checkpoint.rfid_raw_material.source.DataRepository
import com.checkpoint.rfid_raw_material.source.RawMaterialsDatabase
import com.checkpoint.rfid_raw_material.source.db.Provider
import com.checkpoint.rfid_raw_material.source.db.Tags
import com.checkpoint.rfid_raw_material.utils.Reverse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TagsListAdapter(private val dataSet: List<Tags>,private var mainActivity: MainActivity,
                      private val listProvider: List<Provider>,
                      private val onClickListener: OnClickListener):
    RecyclerView.Adapter<TagsListAdapter.ViewHolder>(){

    private var activityMain: MainActivity? = mainActivity
    var reverse= Reverse(activityMain!!.application)

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textTagItem: TextView
        val lyTagIndicator: LinearLayout

        init {
            textTagItem = view.findViewById(R.id.tvItemTag)
            lyTagIndicator= view.findViewById(R.id.lyTagIndicator)
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
        activityMain = activityMain as MainActivity
        Log.e("listProvider","${dataSet[position].epc}")

        reverse.hexToBinary(dataSet[position].epc)
        val idProvider= reverse.getProvider(dataSet[position].epc)

        var validTag= false
        listProvider.iterator().forEachRemaining {
            Log.e("listProvider","${it.id}")
            Log.e("listProvider","${idProvider}")

            if(it.id==idProvider){
                validTag=true
            }
        }.let {
            if (validTag)
                holder.lyTagIndicator.setBackgroundColor(Color.GREEN)
            else
                holder.lyTagIndicator.setBackgroundColor(Color.RED)
        }


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