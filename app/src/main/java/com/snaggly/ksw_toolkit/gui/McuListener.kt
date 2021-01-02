package com.snaggly.ksw_toolkit.gui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.mcu_listener_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(McuListenerViewModel::class.java)
        initElements()
    }

    private fun initElements() {
        sourceSpinner = requireView().findViewById(R.id.mcuSourceSpinner)
        sourceSpinner.adapter = viewModel.getSpinnerAdapter(requireContext())
        sourceSpinner.setSelection(1)

        mcuEventRV = requireView().findViewById(R.id.McuEventsRV);
        mcuEventRV.layoutManager = LinearLayoutManager(context)
        mcuEventRV.adapter = viewModel.getMcuEventAdapter()
    }

}