package com.snaggly.ksw_toolkit.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.snaggly.ksw_toolkit.R

class McuEventRVAdapter : RecyclerView.Adapter<McuEventRVAdapter.ParentViewHolder>() {
    private var names = ArrayList<String>(arrayListOf(""))
    private var datas = ArrayList<String>(arrayListOf(""))

    open inner class ParentViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        open val eventName : TextView? = null
        open val dataString : TextView? = null
    }

    inner class McuEventViewHolder (itemView: View) : ParentViewHolder(itemView) {
        override val eventName : TextView = itemView.findViewById(R.id.mcuEventName)
        override val dataString : TextView = itemView.findViewById(R.id.mcuDataString)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position > 0) 1
        else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): McuEventRVAdapter.ParentViewHolder {
        if (viewType > 0)
            return McuEventViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.mcu_event, parent, false))
        return ParentViewHolder(LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false))
    }

    override fun onBindViewHolder(holder: McuEventRVAdapter.ParentViewHolder, position: Int) {
        holder.eventName?.text = names[position]
        holder.dataString?.text = datas[position]
    }

    override fun getItemCount(): Int {
        return names.size;
    }

    fun addNewEntry(name: String, dataString: String) {
        names.add(1, name)
        datas.add(1, dataString)
        notifyItemInserted(1)
    }
}