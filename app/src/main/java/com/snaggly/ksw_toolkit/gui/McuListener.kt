package com.snaggly.ksw_toolkit.gui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.snaggly.ksw_toolkit.R
import com.snaggly.ksw_toolkit.core.service.McuService
import com.snaggly.ksw_toolkit.gui.viewmodels.McuListenerViewModel

class McuListener(mcuServiceObserver: LiveData<McuService?>) : FragmentMcuServiceView(mcuServiceObserver) {

    init {
        mcuServiceObserver.observe(this, { mcuServiceObj ->
            mcuService = mcuServiceObj
        })
    }

    companion object {
        fun newInstance(mcuServiceObserver: LiveData<McuService?>) = McuListener(mcuServiceObserver)
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
        stopKswServiceSwitch.requestFocus()
        mcuService?.registerMcuEventListener(viewModel.mcuObserver)
    }

    override fun onStop() {
        super.onStop()
        mcuService?.unregisterMcuEventListener(viewModel.mcuObserver)
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