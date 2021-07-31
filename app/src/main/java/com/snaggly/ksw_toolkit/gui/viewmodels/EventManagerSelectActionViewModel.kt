package com.snaggly.ksw_toolkit.gui.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.recyclerview.widget.RecyclerView
import com.snaggly.ksw_toolkit.core.config.ConfigManager
import com.snaggly.ksw_toolkit.core.config.beans.EventManager
import com.snaggly.ksw_toolkit.util.adapters.ListTypeAdapter
import com.snaggly.ksw_toolkit.util.list.app.AppInfo
import com.snaggly.ksw_toolkit.util.list.app.AppsLister
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes
import com.snaggly.ksw_toolkit.util.list.eventtype.EventMode
import com.snaggly.ksw_toolkit.util.list.keyevent.KeyEvent
import com.snaggly.ksw_toolkit.util.list.mcu.McuCommandsList

class EventManagerSelectActionViewModel(application: Application) : AndroidViewModel(application) {
    private var listKeyEventsAdapter : RecyclerView.Adapter<ListTypeAdapter.AppsListViewHolder>? = null
    private var availableAppsAdapter : RecyclerView.Adapter<ListTypeAdapter.AppsListViewHolder>? = null
    private var mcuCommandsListAdapter : RecyclerView.Adapter<ListTypeAdapter.AppsListViewHolder>? = null

    private var keyEvents: ArrayList<KeyEvent>? = null
    private var appsList: ArrayList<AppInfo>? = null
    private var mcuCommandsList: ArrayList<McuCommandsList>? = null
    var config : EventManager? = null

    var eventCurType : EventManagerTypes = EventManagerTypes.Dummy

    lateinit var actionEvent: com.snaggly.ksw_toolkit.gui.EventManager.OnActionResult

    private fun initConfig() {
        if (config == null)
            config = ConfigManager.getConfig(getApplication<Application>().applicationContext.filesDir.absolutePath).eventManagers[eventCurType]
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

    private fun initKeyEventsAdapter() {
        initConfig()
        keyEvents = KeyEvent.getKeyEventList(getApplication<Application>().applicationContext)
        listKeyEventsAdapter = ListTypeAdapter(keyEvents!!, findKeyCodeFromList(config?.keyCode!!.data), onKeyCodeClickListener)
    }

    private fun initAvailableAppsAdapter() {
        appsList = AppsLister(getApplication<Application>().applicationContext).getAppsList()
        availableAppsAdapter = ListTypeAdapter(appsList!!, findAppFromList(config?.appName!!.data), onAppClickListener)
    }

    private fun initMcuCommandsListAdapter() {
        mcuCommandsList = McuCommandsList.getMcuCommandsList(getApplication<Application>().applicationContext)
        mcuCommandsListAdapter = ListTypeAdapter(mcuCommandsList!!, config?.mcuCommandMode!!.data, onMcuCommandClickListener)
    }

    fun getListKeyEventsAdapter(): RecyclerView.Adapter<ListTypeAdapter.AppsListViewHolder> {
        if (listKeyEventsAdapter == null)
            initKeyEventsAdapter()
        return listKeyEventsAdapter!!
    }

    fun getAvailableAppsAdapter() : RecyclerView.Adapter<ListTypeAdapter.AppsListViewHolder> {
        if (availableAppsAdapter == null)
            initAvailableAppsAdapter()
        return availableAppsAdapter!!
    }

    fun getMcuCommandsAdapter() : RecyclerView.Adapter<ListTypeAdapter.AppsListViewHolder> {
        if (mcuCommandsListAdapter == null)
            initMcuCommandsListAdapter()
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