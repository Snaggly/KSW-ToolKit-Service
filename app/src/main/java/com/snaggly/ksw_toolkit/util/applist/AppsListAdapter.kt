package com.snaggly.ksw_toolkit.util.applist

import android.content.Context
import android.database.DataSetObserver
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.snaggly.ksw_toolkit.R

class AppsListAdapter(private val appsList: ArrayList<AppInfo>, private var selectedApp : Int) : RecyclerView.Adapter<AppsListAdapter.AppsListViewHolder>() {

    class AppsListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val appIconView : ImageView = itemView.findViewById(R.id.apps_list_icon)
        val appNameView : TextView = itemView.findViewById(R.id.apps_list_text)
        val appRadioButtonView : RadioButton = itemView.findViewById(R.id.apps_list_radioBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppsListViewHolder {
        return AppsListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.apps_view_list, parent, false))
    }

    override fun onBindViewHolder(holder: AppsListViewHolder, position: Int) {
        val currentApp = appsList[position]
        holder.appIconView.setImageDrawable(currentApp.icon)
        holder.appNameView.text = currentApp.name
        holder.appRadioButtonView.isChecked = position == selectedApp
    }

    override fun getItemCount(): Int {
        return appsList.size
    }

}