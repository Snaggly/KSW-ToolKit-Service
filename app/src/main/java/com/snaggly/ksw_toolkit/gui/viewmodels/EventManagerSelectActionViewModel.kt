package com.snaggly.ksw_toolkit.gui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.snaggly.ksw_toolkit.core.config.ConfigManager
import com.snaggly.ksw_toolkit.core.config.beans.EventManager
import com.snaggly.ksw_toolkit.util.adapters.ListTypeAdapter
import com.snaggly.ksw_toolkit.util.applist.AppInfo
import com.snaggly.ksw_toolkit.util.applist.AppsLister
import com.snaggly.ksw_toolkit.util.enums.EventManagerTypes
import com.snaggly.ksw_toolkit.util.enums.EventMode
import com.snaggly.ksw_toolkit.util.keyevent.KeyEvent
import com.snaggly.ksw_toolkit.util.mcu.McuCommandsList

class EventManagerSelectActionViewModel : ViewModel() {
    private var listKeyEventsAdapter : RecyclerView.Adapter<ListTypeAdapter.AppsListViewHolder>? = null
    private var availableAppsAdapter : RecyclerView.Adapter<ListTypeAdapter.AppsListViewHolder>? = null
    private var mcuCommandsListAdapter : RecyclerView.Adapter<ListTypeAdapter.AppsListViewHolder>? = null

    private var keyEvents: ArrayList<KeyEvent>? = null
    private var appsList: ArrayList<AppInfo>? = null
    private var mcuCommandsList: ArrayList<McuCommandsList>? = null
    var config : EventManager? = null

    var eventCurType : EventManagerTypes = EventManagerTypes.Dummy

    lateinit var actionEvent: com.snaggly.ksw_toolkit.gui.EventManager.OnActionResult

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
        appsList = AppsLister(context).getAppsList()
        availableAppsAdapter = ListTypeAdapter(appsList!!, findAppFromList(config?.appName!!.data), onAppClickListener)
    }

    private fun initMcuCommandsListAdapter(context: Context) {
        mcuCommandsList = McuCommandsList.getMcuCommandsList(context)
        mcuCommandsListAdapter = ListTypeAdapter(mcuCommandsList!!, config?.mcuCommandMode!!.data, onMcuCommandClickListener)
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

    fun getMcuCommandsAdapter(context: Context) : RecyclerView.Adapter<ListTypeAdapter.AppsListViewHolder> {
        if (mcuCommandsListAdapter == null)
            initMcuCommandsListAdapter(context)
        return mcuCommandsListAdapter!!
    }

    fun resetEvent() {
        config?.eventMode = EventMode.NoAssignment
        config?.keyCode!!.data = -1
        config?.appName!!.data = ""
        config?.mcuCommandMode!!.data = -1
    }

    private val onKeyCodeClickListener = object : ListTypeAdapter.OnAppClickListener {
        override fun onAppClick(position: Int) {
            config?.eventMode = EventMode.KeyEvent
            config?.keyCode!!.data = keyEvents?.get(position)!!.code
            config?.appName!!.data = ""
            config?.mcuCommandMode!!.data = -1
            actionEvent.notifyView()
        }
    }

    private val onAppClickListener = object : ListTypeAdapter.OnAppClickListener {
        override fun onAppClick(position: Int) {
            config?.eventMode = EventMode.StartApp
            config?.keyCode!!.data = -1
            config?.appName!!.data = appsList?.get(position)!!.packageName
            config?.mcuCommandMode!!.data = -1
            actionEvent.notifyView()
        }
    }

    private val onMcuCommandClickListener = object : ListTypeAdapter.OnAppClickListener {
        override fun onAppClick(position: Int) {
            config?.eventMode = EventMode.McuCommand
            config?.keyCode!!.data = -1
            config?.appName!!.data = ""
            config?.mcuCommandMode!!.data = position
            actionEvent.notifyView()
        }
    }
}