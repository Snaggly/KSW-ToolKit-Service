package com.snaggly.ksw_toolkit.gui.viewmodels

import android.app.Activity
import android.content.Context
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.snaggly.ksw_toolkit.R
import com.snaggly.ksw_toolkit.core.config.ConfigManager
import com.snaggly.ksw_toolkit.core.config.beans.McuListener
import com.snaggly.ksw_toolkit.core.service.helper.CoreServiceClient
import com.snaggly.ksw_toolkit.core.service.mcu.McuEventObserver
import com.snaggly.ksw_toolkit.util.adapters.McuEventRVAdapter
import com.snaggly.ksw_toolkit.util.enums.EventManagerTypes
import projekt.auto.mcu.ksw.serial.collection.McuEvent

class McuListenerViewModel : CoreServiceClient() {

    private var mcuEventRVAdapter: McuEventRVAdapter = McuEventRVAdapter()
    private var spinnerAdapter: ArrayAdapter<CharSequence>? = null
    private var isShowing = true
    var config: McuListener? = null

    override fun onCleared() {
        isShowing = false
        super.onCleared()
    }

    fun getSpinnerAdapter(context: Context): ArrayAdapter<CharSequence> {
        if (spinnerAdapter == null) {
            config = ConfigManager.getConfig(context.filesDir.absolutePath).mcuListener
            spinnerAdapter = ArrayAdapter.createFromResource(context, R.array.mcuSourcesList, R.layout.spinner_layout)
            spinnerAdapter!!.setDropDownViewResource(R.layout.spinner_dropdown_layout)
        }
        return spinnerAdapter!!
    }

    fun getMcuEventAdapter(): RecyclerView.Adapter<out RecyclerView.ViewHolder> {
        return mcuEventRVAdapter
    }

    private fun addEntryToAdapter(eventName: String, dataString: String) {
        mcuEventRVAdapter.addNewEntry(eventName, dataString)
    }

    fun dataBytesToString(data: ByteArray): String {
        var result = ""
        for (i in 0..data.size - 2) {
            result += data[i].toString(16) + "-"
        }
        result += data[data.size - 1].toString(16)

        return result
    }

    var parentActivity: Activity? = null

    val mcuObserver = object : McuEventObserver {
        override fun update(eventType: EventManagerTypes?, cmdType: Int, data: ByteArray) {
            val eventName = eventType?.name ?: "Unknown Event"
            parentActivity?.runOnUiThread {
                addEntryToAdapter("$eventName - $cmdType", dataBytesToString(data))
            }
        }
    }
}