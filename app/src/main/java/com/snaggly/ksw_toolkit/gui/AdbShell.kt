package com.snaggly.ksw_toolkit.gui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.lifecycle.LiveData
import com.google.android.material.textfield.TextInputEditText
import com.snaggly.ksw_toolkit.R
import com.snaggly.ksw_toolkit.core.service.McuService

class AdbShell(mcuServiceObserver: LiveData<McuService?>) : FragmentMcuServiceView(mcuServiceObserver) {

    init {
        mcuServiceObserver.observe(this, { mcuServiceObj ->
            mcuService = mcuServiceObj
        })
    }

    private val adbShellObserver = object : McuService.ShellObserver {
        override fun update() {
            requireActivity().runOnUiThread {
                listAdapter.notifyDataSetChanged()
            }
        }
    }

    companion object {
        fun newInstance(mcuService: LiveData<McuService?>) = AdbShell(mcuService)
    }

    private lateinit var shellListView: ListView
    private lateinit var textInput: TextInputEditText
    private lateinit var sendButton: Button
    private lateinit var listAdapter: ArrayAdapter<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.adb_shell_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initElements()
        initClickEvent()
    }

    override fun onStart() {
        super.onStart()
        sendButton.requestFocus()
        mcuService?.registerShellListener(adbShellObserver)
    }

    override fun onStop() {
        super.onStop()
        mcuService?.unregisterShellListener(adbShellObserver)
    }

    private fun initElements() {
        shellListView = requireView().findViewById(R.id.adbShellStdoutListView)
        textInput = requireView().findViewById(R.id.adbEditText)
        sendButton = requireView().findViewById(R.id.adbSend)
    }

    private fun initList() {
        listAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mcuService?.adbLines!!)
        shellListView.adapter = listAdapter
    }

    private fun initClickEvent() {
        sendButton.setOnClickListener {
            mcuService?.sendAdbCommand(textInput.text.toString())
            Log.d("Snaggly", "Sent Adb command: ${textInput.text.toString()}")
            textInput.setText("")
        }
    }
}