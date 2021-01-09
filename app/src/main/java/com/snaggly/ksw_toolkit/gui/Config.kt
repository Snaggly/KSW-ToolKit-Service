package com.snaggly.ksw_toolkit.gui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import com.snaggly.ksw_toolkit.R
import com.snaggly.ksw_toolkit.core.service.McuService
import com.snaggly.ksw_toolkit.gui.viewmodels.ConfigViewModel

class Config(mcuServiceObserver: LiveData<McuService?>) : FragmentMcuServiceView(mcuServiceObserver) {

    init {
        mcuServiceObserver.observe(this, { mcuServiceObj ->
            mcuService = mcuServiceObj
        })
    }

    companion object {
        fun newInstance(mcuServiceObserver: LiveData<McuService?>) = Config(mcuServiceObserver)
    }

    private lateinit var viewModel: ConfigViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.config_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ConfigViewModel::class.java)
        // TODO: Use the ViewModel
    }

}