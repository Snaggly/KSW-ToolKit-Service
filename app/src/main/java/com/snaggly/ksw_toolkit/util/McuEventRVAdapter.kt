package com.snaggly.ksw_toolkit.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.snaggly.ksw_toolkit.R

class McuEventRVAdapter : RecyclerView.Adapter<McuEventRVAdapter.McuEventViewHolder>() {

    inner class McuEventViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventName : TextView = itemView.findViewById(R.id.mcuEventName)
        val dataString : TextView = itemView.findViewById(R.id.mcuDataString)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): McuEventRVAdapter.McuEventViewHolder {
        return McuEventViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.mcu_event, parent, false))
    }

    override fun onBindViewHolder(holder: McuEventRVAdapter.McuEventViewHolder, position: Int) {
        holder.eventName.text = "Received Can Data"
        holder.dataString.text = "A1-1C-31-D1-31-76-72-91-00-12-B6-80-14-41-21-31"
    }

    override fun getItemCount(): Int {
        return 5;
    }
}