package com.snaggly.ksw_toolkit.gui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.snaggly.ksw_toolkit.core.config.ConfigManager
import com.snaggly.ksw_toolkit.core.config.beans.EventManager
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes

class EventManagerViewModel : ViewModel() {
    private var config : HashMap<EventManagerTypes, EventManager>? = null

    fun getConfig(context: Context) : HashMap<EventManagerTypes, EventManager> {
        if (config == null)
            config = ConfigManager.getConfig(context.filesDir.absolutePath).eventManagers
        return config!!
    }


}