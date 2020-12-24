package com.snaggly.ksw_toolkit.gui.viewmodels

import android.R
import android.content.Context
import android.widget.Adapter
import androidx.lifecycle.ViewModel
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.snaggly.ksw_toolkit.util.applist.AppsListAdapter
import com.snaggly.ksw_toolkit.util.applist.AppsLister
import com.snaggly.ksw_toolkit.util.applist.KeyEventMock
import com.snaggly.ksw_toolkit.util.enums.KeyCode
import java.util.ArrayList

class EventManagerSelectActionViewModel : ViewModel() {
    private var listKeyEventsAdapter : RecyclerView.Adapter<AppsListAdapter.AppsListViewHolder>? = null
    private var availableAppsAdapter : RecyclerView.Adapter<AppsListAdapter.AppsListViewHolder>? = null

    private fun initKeyEventsAdapter(context: Context, defaultSelection: Int) {
        listKeyEventsAdapter = AppsListAdapter(KeyEventMock.getMockedAppList(context), defaultSelection)
    }

    private fun initAvailableAppsAdapter(context: Context, defaultSelection: Int) {
        val appNames = AppsLister(context).getAppsList()
        availableAppsAdapter = AppsListAdapter(appNames, defaultSelection)
    }

    fun getListKeyEventsAdapter(context: Context): RecyclerView.Adapter<AppsListAdapter.AppsListViewHolder>? {
        if (listKeyEventsAdapter == null)
            initKeyEventsAdapter(context, 0)
        return listKeyEventsAdapter!!
    }

    fun getAvailableAppsAdapter(context: Context) : RecyclerView.Adapter<AppsListAdapter.AppsListViewHolder> {
        if (availableAppsAdapter == null)
            initAvailableAppsAdapter(context, 0)
        return availableAppsAdapter!!
    }
}