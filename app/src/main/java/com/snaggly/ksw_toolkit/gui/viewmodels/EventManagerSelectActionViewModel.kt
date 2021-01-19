package com.snaggly.ksw_toolkit.gui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.snaggly.ksw_toolkit.core.config.ConfigManager
import com.snaggly.ksw_toolkit.core.config.beans.EventManager
import com.snaggly.ksw_toolkit.util.ListTypeAdapter
import com.snaggly.ksw_toolkit.util.applist.AppInfo
import com.snaggly.ksw_toolkit.util.applist.AppsLister
import com.snaggly.ksw_toolkit.util.enums.EventManagerTypes
import com.snaggly.ksw_toolkit.util.enums.EventMode
import com.snaggly.ksw_toolkit.util.keyevent.KeyEvent

class EventManagerSelectActionViewModel : ViewModel() {
    private var listKeyEventsAdapter : RecyclerView.Adapter<ListTypeAdapter.AppsListViewHolder>? = null
    private var availableAppsAdapter : RecyclerView.Adapter<ListTypeAdapter.AppsListViewHolder>? = null

    private var keyEvents: ArrayList<KeyEvent>? = null
    private var appsList: ArrayList<AppInfo>? = null
    private var config : EventManager? = null

    var eventCurType : EventManagerTypes = EventManagerTypes.Dummy

    private fun initConfig(context: Context) {
        if (config == null)
            config = ConfigManager.getConfig(context.filesDir.absolutePath).eventManagers[eventCurType]
    }

    private fun findKeyCodeFromList(keyCode: Int) : Int{
        for (i in 0 until keyEvents?.size!!) {
            if (keyCode == keyEvents!![i].code)
                return i
        }
        return -1
    }

    private fun findAppFromList(name: String) : Int{
        for (i in 0 until appsList?.size!!) {
            if (name == appsList!![i].packageName)
                return i
        }
        return -1
    }

    private fun initKeyEventsAdapter(context: Context) {
        initConfig(context)
        keyEvents = KeyEvent.getKeyEventList(context)
        listKeyEventsAdapter = ListTypeAdapter(keyEvents!!, findKeyCodeFromList(config?.keyCode!!.data), onKeyCodeClickListener)
    }

    private fun initAvailableAppsAdapter(context: Context) {
        initConfig(context)
        appsList = AppsLister(context).getAppsList()
        availableAppsAdapter = ListTypeAdapter(appsList!!, findAppFromList(config?.appName!!.data), onAppClickListener)
    }

    fun getListKeyEventsAdapter(context: Context): RecyclerView.Adapter<ListTypeAdapter.AppsListViewHolder> {
        if (listKeyEventsAdapter == null)
            initKeyEventsAdapter(context)
        return listKeyEventsAdapter!!
    }

    fun getAvailableAppsAdapter(context: Context) : RecyclerView.Adapter<ListTypeAdapter.AppsListViewHolder> {
        if (availableAppsAdapter == null)
            initAvailableAppsAdapter(context)
        return availableAppsAdapter!!
    }

    private val onKeyCodeClickListener = object : ListTypeAdapter.OnAppClickListener {
        override fun onAppClick(position: Int) {
            config?.eventMode = EventMode.KeyEvent
            config?.keyCode!!.data = keyEvents?.get(position)!!.code
        }
    }

    private val onAppClickListener = object : ListTypeAdapter.OnAppClickListener {
        override fun onAppClick(position: Int) {
            config?.eventMode = EventMode.StartApp
            config?.appName!!.data = appsList?.get(position)!!.packageName
        }
    }
}