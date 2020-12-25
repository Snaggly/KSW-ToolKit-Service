package com.snaggly.ksw_toolkit.util

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.snaggly.ksw_toolkit.R

class ListTypeAdapter<T : ListType>(private val appsList: ArrayList<T>,
                                    private var selectedApp : Int,
                                    private val onAppClickListener: OnAppClickListener)
    : RecyclerView.Adapter<ListTypeAdapter<T>.AppsListViewHolder>() {

    private lateinit var previousSelection : AppsListViewHolder

    interface OnAppClickListener { fun onAppClick(position: Int)}

    inner class AppsListViewHolder(itemView: View, private val onClickListener: OnAppClickListener)
        : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnFocusChangeListener {
        val appIconView : ImageView = itemView.findViewById(R.id.apps_list_icon)
        val appNameView : TextView = itemView.findViewById(R.id.apps_list_text)
        val appRadioButtonView : RadioButton = itemView.findViewById(R.id.apps_list_radioBtn)

        init {
            itemView.setOnClickListener(this)
            itemView.onFocusChangeListener = this
        }

        override fun onClick(v: View?) {
            previousSelection.appRadioButtonView.isChecked = false
            appRadioButtonView.isChecked = true
            previousSelection = this
            onClickListener.onAppClick(adapterPosition)
        }

        override fun onFocusChange(v: View?, hasFocus: Boolean) {
            if (hasFocus)
                appRadioButtonView.buttonTintList = ColorStateList.valueOf(itemView.context.getColor(R.color.purple_200))
            else
                appRadioButtonView.buttonTintList = ColorStateList.valueOf(itemView.context.getColor(R.color.purple_500))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppsListViewHolder {
        return AppsListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.apps_view_list, parent, false), onAppClickListener)
    }

    override fun onBindViewHolder(holder: AppsListViewHolder, position: Int) {
        val currentApp = appsList[position]
        holder.appIconView.setImageDrawable(currentApp.icon)
        holder.appNameView.text = currentApp.name
        if (position == selectedApp) {
            holder.appRadioButtonView.isChecked = true
            holder.itemView.requestFocus()
            previousSelection = holder
        }
    }

    override fun getItemCount(): Int {
        return appsList.size
    }
}