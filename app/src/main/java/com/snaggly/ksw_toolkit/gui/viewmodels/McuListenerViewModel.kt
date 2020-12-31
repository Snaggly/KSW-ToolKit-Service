package com.snaggly.ksw_toolkit.gui.viewmodels

import android.app.Activity
import android.content.Context
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.snaggly.ksw_toolkit.R
import com.snaggly.ksw_toolkit.util.McuEventRVAdapter

class McuListenerViewModel : ViewModel() {

    private var mcuEventRVAdapter: McuEventRVAdapter = McuEventRVAdapter()
    private var spinnerAdapter : ArrayAdapter<String>? = null
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

    fun getSpinnerAdapter(context: Context) : ArrayAdapter<String> {
        if (spinnerAdapter==null) {
            spinnerAdapter = ArrayAdapter<String>(context, R.layout.spinner_layout, sources)
            spinnerAdapter!!.setDropDownViewResource(R.layout.spinner_dropdown_layout)
        }
        return spinnerAdapter!!
    }

    fun getMcuEventAdapter() : RecyclerView.Adapter<out RecyclerView.ViewHolder> {
        return mcuEventRVAdapter
    }

    private fun addEntryToAdapter(eventName: String, dataString: String) {
        mcuEventRVAdapter.addNewEntry(eventName, dataString)
    }

    var counter = 1
    fun testRV(parentActivity: Activity) {
        Thread {
            while(isShowing) {
                parentActivity.runOnUiThread {
                    addEntryToAdapter("Test Event ${counter++}", "A1-1C-23-21-BA-13-F2-2D-32-13-76-13-65-F2-A4-13-42-13")
                }
                Thread.sleep(1000)
            }
        }.start()
    }

}