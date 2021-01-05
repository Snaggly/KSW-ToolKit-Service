package com.snaggly.ksw_toolkit.gui

import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import com.google.android.material.textfield.TextInputEditText
import com.snaggly.ksw_toolkit.R
import com.snaggly.ksw_toolkit.core.service.McuService

class AdbShell : Fragment() {

    private var isBound = false
    private lateinit var mcuService: McuService

    private val connection = object : ServiceConnection {
        private var registeredAt : Int = 0

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            mcuService = (service as McuService.McuServiceBinder).getService()
            registeredAt = mcuService.registerShellListener(adbShellObserver)
            isBound = true
            Log.d("Snaggly", "AdbShell connected to Service at $registeredAt")
            initList()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mcuService.unregisterShellListener(registeredAt)
            isBound = false
            Log.d("Snaggly", "AdbShell disconnected from Service at $registeredAt")
        }
    }

    val adbShellObserver = object : McuService.ShellObserver {
        override fun update() {
            requireActivity().runOnUiThread {
                listAdapter.notifyDataSetChanged()
            }
        }
    }

    companion object {
        fun newInstance() = AdbShell()
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
        Intent(requireContext(), McuService::class.java).also { intent ->
            requireActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
        Log.d("Snaggly", "AdbShell binding to Service")
        sendButton.requestFocus()
    }

    override fun onStop() {
        super.onStop()
        requireActivity().unbindService(connection)
        Log.d("Snaggly", "AdbShell unbinding from Service")
    }

    private fun initElements() {
        shellListView = requireView().findViewById(R.id.adbShellStdoutListView)
        textInput = requireView().findViewById(R.id.adbEditText)
        sendButton = requireView().findViewById(R.id.adbSend)
    }

    private fun initList() {
        listAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mcuService.adbLines)
        shellListView.adapter = listAdapter
    }

    private fun initClickEvent() {
        sendButton.setOnClickListener {
            if (!isBound) {
                AlertDialog.Builder(requireContext()).setTitle("KSW-ToolKit AdbShell").setMessage("This fragment was not bound to the Core Service.\nOperation failed!").show()
            } else {
                mcuService.sendAdbCommand(textInput.text.toString())
                Log.d("Snaggly", "Sent Adb command: ${textInput.text.toString()}")
                textInput.setText("")
            }
        }
    }
}