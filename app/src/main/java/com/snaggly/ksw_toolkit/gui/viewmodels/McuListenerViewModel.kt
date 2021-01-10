package com.snaggly.ksw_toolkit.gui.viewmodels

import android.app.Activity
import android.content.Context
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.snaggly.ksw_toolkit.R
import com.snaggly.ksw_toolkit.core.service.McuService
import com.snaggly.ksw_toolkit.core.service.helper.McuServiceClient
import com.snaggly.ksw_toolkit.util.McuEventRVAdapter
import projekt.auto.mcu.ksw.serial.McuEvent

class McuListenerViewModel : McuServiceClient() {

    private var mcuEventRVAdapter: McuEventRVAdapter = McuEventRVAdapter()
    private var spinnerAdapter: ArrayAdapter<String>? = null
    private var isShowing = true
    private var sources: Array<String> = arrayOf(
            "/dev/ttyMSM0",
            "/dev/ttyMSM1",
            "/dev/ttyS0",
            "/dev/ttyS1",
            "/dev/ttyS2",
            "/dev/ttyS3")

    override fun onCleared() {
        isShowing = false
        super.onCleared()
    }

    fun getSpinnerAdapter(context: Context): ArrayAdapter<String> {
        if (spinnerAdapter == null) {
            spinnerAdapter = ArrayAdapter<String>(context, R.layout.spinner_layout, sources)
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

    val mcuObserver = object : McuService.McuEventObserver {
        override fun update(eventType: McuEvent?, cmdType: Int, data: ByteArray) {
            val eventName = eventType?.name ?: "Unknown Event"
            parentActivity?.runOnUiThread {
                addEntryToAdapter("$eventName - $cmdType", dataBytesToString(data))
            }
        }

    }
}