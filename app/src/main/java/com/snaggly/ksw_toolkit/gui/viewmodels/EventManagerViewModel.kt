package com.snaggly.ksw_toolkit.gui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.snaggly.ksw_toolkit.core.config.ConfigManager
import com.snaggly.ksw_toolkit.core.config.beans.EventManager
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes

class EventManagerViewModel(application: Application) : AndroidViewModel(application) {
    private var config : HashMap<EventManagerTypes, EventManager>? = null

    fun getConfig() : HashMap<EventManagerTypes, EventManager> {
        if (config == null)
            config = ConfigManager.getConfig(getApplication<Application>().applicationContext.filesDir.absolutePath).eventManagers
        return config!!
    }


}