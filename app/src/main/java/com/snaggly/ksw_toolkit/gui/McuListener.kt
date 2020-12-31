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
    private var threadRunning: Boolean = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.mcu_listener_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(McuListenerViewModel::class.java)
        threadRunning = true
        initElements()
    }

    override fun onStop() {
        threadRunning = false
        super.onStop()
    }

    private fun initElements() {
        sourceSpinner = requireView().findViewById(R.id.mcuSourceSpinner)
        sourceSpinner.adapter = viewModel.getSpinnerAdapter(requireContext())
        sourceSpinner.setSelection(1)

        mcuEventRV = requireView().findViewById(R.id.McuEventsRV);
        mcuEventRV.layoutManager = LinearLayoutManager(context)
        mcuEventRV.adapter = viewModel.getMcuEventAdapter()

        Thread {
            var counter = 1
            while(threadRunning) {
                requireActivity().runOnUiThread {
                    viewModel.addEntryToAdapter("Test Event ${counter++}", "A1-1C-23-21-BA-13-F2-2D-32-13-76-13-65-F2-A4-13-42-13")
                }
                Thread.sleep(1000)
            }
        }.start()
    }

}