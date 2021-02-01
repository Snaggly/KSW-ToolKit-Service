package com.snaggly.ksw_toolkit.gui.viewmodels

import android.app.Activity
import androidx.recyclerview.widget.RecyclerView
import com.snaggly.ksw_toolkit.core.service.helper.CoreServiceClient
import com.snaggly.ksw_toolkit.core.service.mcu.McuEventObserver
import com.snaggly.ksw_toolkit.util.adapters.McuEventRVAdapter
import com.snaggly.ksw_toolkit.util.enums.EventManagerTypes

class McuListenerViewModel : CoreServiceClient() {

    private var mcuEventRVAdapter: McuEventRVAdapter = McuEventRVAdapter()

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
            if (data.size > 2)
                if (cmdType == 0xA1 && data[0] == 0x17.toByte() && data[2] == 0x0.toByte())
                        return
            val eventName = eventType?.name ?: "Unknown Event"
            parentActivity?.runOnUiThread {
                addEntryToAdapter("$eventName - $cmdType", dataBytesToString(data))
            }
        }
    }
}