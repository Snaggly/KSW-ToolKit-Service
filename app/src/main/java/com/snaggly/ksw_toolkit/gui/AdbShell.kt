package com.snaggly.ksw_toolkit.gui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.snaggly.ksw_toolkit.gui.viewmodels.AdbShellViewModel
import com.snaggly.ksw_toolkit.R

class AdbShell : Fragment() {

    companion object {
        fun newInstance() = AdbShell()
    }

    private lateinit var viewModel: AdbShellViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.adb_shell_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AdbShellViewModel::class.java)
        // TODO: Use the ViewModel
    }

}