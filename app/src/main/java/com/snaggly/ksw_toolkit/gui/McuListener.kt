package com.snaggly.ksw_toolkit.gui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.snaggly.ksw_toolkit.R
import com.snaggly.ksw_toolkit.core.service.mcu.McuEventObserver
import com.snaggly.ksw_toolkit.gui.viewmodels.McuListenerViewModel
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes

class McuListener : Fragment() {

    companion object {
        fun newInstance() = McuListener()
    }

    private lateinit var viewModel: McuListenerViewModel
    private lateinit var mcuEventRV: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.mcu_listener_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(McuListenerViewModel::class.java)
        initElements()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        viewModel.coreService?.mcuReaderHandler?.registerMcuEventListener(mcuObserver)
    }

    override fun onStop() {
        super.onStop()
        viewModel.coreService?.mcuReaderHandler?.unregisterMcuEventListener()
    }

    private fun initElements() {
        mcuEventRV = requireView().findViewById(R.id.McuEventsRV);
        mcuEventRV.layoutManager = LinearLayoutManager(context)
        mcuEventRV.adapter = viewModel.getMcuEventAdapter()
    }

    private val mcuObserver = object : McuEventObserver {
        override fun update(eventType: EventManagerTypes?, cmdType: Int, data: ByteArray) {
            if (data.size > 2)
                if (cmdType == 0xA1 && data[0] == 0x17.toByte() && data[2] == 0x0.toByte())
                    return
            val eventName = eventType?.name ?: "Unknown Event"
            requireActivity().runOnUiThread {
                viewModel.addEntryToAdapter("$eventName - $cmdType", viewModel.dataBytesToString(data))
            }
        }
    }
}