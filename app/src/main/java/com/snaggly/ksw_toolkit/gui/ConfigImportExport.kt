package com.snaggly.ksw_toolkit.gui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.snaggly.ksw_toolkit.R
import com.snaggly.ksw_toolkit.gui.viewmodels.ConfigImportExportViewModel

class ConfigImportExport : Fragment() {

    companion object {
        fun newInstance() = ConfigImportExport()
    }

    private lateinit var importExportViewModel: ConfigImportExportViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.config_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        importExportViewModel = ViewModelProvider(this).get(ConfigImportExportViewModel::class.java)
        // TODO: Use the ViewModel
    }

}