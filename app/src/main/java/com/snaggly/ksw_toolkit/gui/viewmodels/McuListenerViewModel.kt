package com.snaggly.ksw_toolkit.gui.viewmodels

import android.content.Context
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModel
import com.snaggly.ksw_toolkit.R

class McuListenerViewModel : ViewModel() {

    private var adapter : ArrayAdapter<String>? = null
    private var sources: Array<String> = arrayOf(
            "/dev/ttyMSM0",
            "/dev/ttyMSM1",
            "/dev/ttyS0",
            "/dev/ttyS1",
            "/dev/ttyS2",
            "/dev/ttyS3")

    fun getSpinnerAdapter(context: Context) : ArrayAdapter<String> {
        if (adapter==null) {
            adapter = ArrayAdapter<String>(context, R.layout.spinner_layout, sources)
            adapter!!.setDropDownViewResource(R.layout.spinner_dropdown_layout)
        }
        return adapter!!
    }
}