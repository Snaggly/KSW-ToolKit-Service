package com.snaggly.ksw_toolkit.gui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.snaggly.ksw_toolkit.util.ListType
import com.snaggly.ksw_toolkit.util.ListTypeAdapter
import com.snaggly.ksw_toolkit.util.applist.AppsLister
import com.snaggly.ksw_toolkit.util.keyevent.KeyEvent

class EventManagerSelectActionViewModel : ViewModel() {
    private var listKeyEventsAdapter : RecyclerView.Adapter<ListTypeAdapter.AppsListViewHolder>? = null
    private var availableAppsAdapter : RecyclerView.Adapter<ListTypeAdapter.AppsListViewHolder>? = null

    private fun initKeyEventsAdapter(context: Context, defaultSelection: Int) {
        listKeyEventsAdapter = ListTypeAdapter(KeyEvent.getKeyEventList(context), defaultSelection)
    }

    private fun initAvailableAppsAdapter(context: Context, defaultSelection: Int) {
        val appNames = AppsLister(context).getAppsList()
        availableAppsAdapter = ListTypeAdapter(appNames, defaultSelection)
    }

    fun getListKeyEventsAdapter(context: Context): RecyclerView.Adapter<ListTypeAdapter.AppsListViewHolder>? {
        if (listKeyEventsAdapter == null)
            initKeyEventsAdapter(context, 0)
        return listKeyEventsAdapter!!
    }

    fun getAvailableAppsAdapter(context: Context) : RecyclerView.Adapter<ListTypeAdapter.AppsListViewHolder> {
        if (availableAppsAdapter == null)
            initAvailableAppsAdapter(context, 0)
        return availableAppsAdapter!!
    }
}