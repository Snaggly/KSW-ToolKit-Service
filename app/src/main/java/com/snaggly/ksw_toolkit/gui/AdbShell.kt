package com.snaggly.ksw_toolkit.gui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.snaggly.ksw_toolkit.R
import com.snaggly.ksw_toolkit.core.service.CoreService
import com.snaggly.ksw_toolkit.core.service.adb.ShellObserver
import com.snaggly.ksw_toolkit.gui.viewmodels.AdbShellViewModel

class AdbShell : Fragment() {

    private val adbShellObserver = object : ShellObserver {
        override fun update(newLine: String) {
            requireActivity().runOnUiThread {
                adbLines[0] += newLine
                listAdapter.notifyDataSetChanged()
            }
        }
    }

    companion object {
        fun newInstance() = AdbShell()
    }

    private lateinit var viewModel: AdbShellViewModel
    private lateinit var shellListView: ListView
    private lateinit var textInput: TextInputEditText
    private lateinit var sendButton: Button
    private lateinit var listAdapter: ArrayAdapter<String>
    private val adbLines : ArrayList<String> = arrayListOf("")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.adb_shell_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AdbShellViewModel::class.java)
        initElements()
        initClickEvent()
    }

    override fun onStart() {
        super.onStart()
        sendButton.requestFocus()
        initList()
        viewModel.coreService?.adbConnection!!.registerShellListener(adbShellObserver)
    }

    override fun onStop() {
        super.onStop()
        viewModel.coreService?.adbConnection!!.unregisterShellListener(adbShellObserver)
    }

    private fun initElements() {
        shellListView = requireView().findViewById(R.id.adbShellStdoutListView)
        textInput = requireView().findViewById(R.id.adbEditText)
        sendButton = requireView().findViewById(R.id.adbSend)
    }

    private fun initList() {
        listAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, adbLines)
        shellListView.adapter = listAdapter
    }

    private fun initClickEvent() {
        sendButton.setOnClickListener {
            viewModel.coreService?.adbConnection!!.sendCommand(textInput.text.toString())
            Log.d("Snaggly", "Sent Adb command: ${textInput.text.toString()}")
            textInput.setText("")
        }
    }
}