package com.snaggly.ksw_toolkit.gui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.snaggly.ksw_toolkit.R
import com.snaggly.ksw_toolkit.core.service.McuService
import com.snaggly.ksw_toolkit.gui.viewmodels.McuListenerViewModel

class McuListener : Fragment() {
    private var isBound = false
    private lateinit var mcuService: McuService

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            mcuService = (service as McuService.McuServiceBinder).getService()
            mcuService.registerMcuEventListener(viewModel.mcuObserver)
            isBound = true
            Log.d("Snaggly", "McuListener connected to Service")
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mcuService.unregisterMcuEventListener(viewModel.mcuObserver)
            isBound = false
            Log.d("Snaggly", "McuListener disconnected from Service")
        }
    }

    companion object {
        fun newInstance() = McuListener()
    }

    private lateinit var viewModel: McuListenerViewModel
    private lateinit var sourceSpinner: Spinner
    private lateinit var mcuEventRV: RecyclerView
    private lateinit var stopKswServiceSwitch: SwitchCompat
    private lateinit var stopKswServiceOnBootSwitch: SwitchCompat

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
        viewModel.parentActivity = requireActivity()
        Intent(requireContext(), McuService::class.java).also { intent ->
            requireActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
        Log.d("Snaggly", "McuListener binding to Service")
        stopKswServiceSwitch.requestFocus()
    }

    override fun onStop() {
        super.onStop()
        requireActivity().unbindService(connection)
        Log.d("Snaggly", "McuListener unbinding from Service")
    }

    private fun initElements() {
        sourceSpinner = requireView().findViewById(R.id.mcuSourceSpinner)
        sourceSpinner.adapter = viewModel.getSpinnerAdapter(requireContext())
        sourceSpinner.setSelection(1)

        mcuEventRV = requireView().findViewById(R.id.McuEventsRV);
        mcuEventRV.layoutManager = LinearLayoutManager(context)
        mcuEventRV.adapter = viewModel.getMcuEventAdapter()

        stopKswServiceSwitch = requireView().findViewById(R.id.startStopKSWServiceSwitch)
        stopKswServiceOnBootSwitch = requireView().findViewById(R.id.startStopKSWServiceOnBootSwitch)
    }

}