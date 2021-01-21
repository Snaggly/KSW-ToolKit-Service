package com.snaggly.ksw_toolkit.gui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.CompoundButton
import android.widget.Spinner
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.snaggly.ksw_toolkit.R
import com.snaggly.ksw_toolkit.gui.viewmodels.McuListenerViewModel

class McuListener : Fragment() {

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
        initButtonClickEvents()
    }

    override fun onStart() {
        super.onStart()
        viewModel.parentActivity = requireActivity()
        stopKswServiceSwitch.requestFocus()
        viewModel.coreService?.mcuReader?.registerMcuEventListener(viewModel.mcuObserver)
    }

    override fun onStop() {
        super.onStop()
        viewModel.coreService?.mcuReader?.unregisterMcuEventListener(viewModel.mcuObserver)
    }

    private fun initElements() {
        sourceSpinner = requireView().findViewById(R.id.mcuSourceSpinner)
        sourceSpinner.adapter = viewModel.getSpinnerAdapter(requireContext())
        sourceSpinner.setSelection(viewModel.config?.mcuSource!!.data)

        mcuEventRV = requireView().findViewById(R.id.McuEventsRV);
        mcuEventRV.layoutManager = LinearLayoutManager(context)
        mcuEventRV.adapter = viewModel.getMcuEventAdapter()

        stopKswServiceSwitch = requireView().findViewById(R.id.startStopKSWServiceSwitch)
        stopKswServiceOnBootSwitch = requireView().findViewById(R.id.startStopKSWServiceOnBootSwitch)
    }

    private fun initButtonClickEvents() {
        sourceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.config?.mcuSource!!.data = position
                viewModel.coreService?.mcuReader?.restartReader()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) { }
        }

        stopKswServiceSwitch.setOnCheckedChangeListener { _: CompoundButton, b: Boolean ->
            if (b)
                viewModel.startKsw()
            else
                viewModel.stopKsw()
        }

        stopKswServiceOnBootSwitch.setOnCheckedChangeListener { _: CompoundButton, b: Boolean ->
            viewModel.config?.stopKswServiceOnBoot!!.data = b
        }
    }
}