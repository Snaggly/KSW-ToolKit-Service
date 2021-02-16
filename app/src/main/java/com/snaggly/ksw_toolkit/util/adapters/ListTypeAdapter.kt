package com.snaggly.ksw_toolkit.util.adapters

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.snaggly.ksw_toolkit.R
import com.snaggly.ksw_toolkit.util.list.ListType

class ListTypeAdapter(private val appsList: ArrayList<out ListType>,
                      private var selectedApp : Int,
                      private val onAppClickListener: OnAppClickListener)
    : RecyclerView.Adapter<ListTypeAdapter.AppsListViewHolder>() {

    private lateinit var purple200 : ColorStateList
    private lateinit var purple500 : ColorStateList
    private var previousSelection : AppsListViewHolder? = null

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
            previousSelection?.appRadioButtonView?.isChecked = false
            appRadioButtonView.isChecked = true
            previousSelection = this
            selectedApp = adapterPosition
            onClickListener.onAppClick(adapterPosition)
        }

        override fun onFocusChange(v: View?, hasFocus: Boolean) {
            if (hasFocus)
                appRadioButtonView.buttonTintList = purple200
            else
                appRadioButtonView.buttonTintList = purple500
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppsListViewHolder {
        purple200 = ColorStateList.valueOf(parent.context.getColor(R.color.purple_200))
        purple500 = ColorStateList.valueOf(parent.context.getColor(R.color.purple_500))
        return AppsListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.apps_view_list, parent, false), onAppClickListener)
    }

    override fun onBindViewHolder(holder: AppsListViewHolder, position: Int) {
        val currentApp = appsList[position]
        holder.appIconView.setImageDrawable(currentApp.icon)
        holder.appNameView.text = currentApp.name
        if (position == selectedApp) {
            holder.appRadioButtonView.isChecked = true
            previousSelection = holder
        }
        else {
            holder.appRadioButtonView.isChecked = false
        }
    }

    override fun getItemCount(): Int {
        return appsList.size
    }
}