package com.snaggly.ksw_toolkit.gui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import com.snaggly.ksw_toolkit.R
import com.snaggly.ksw_toolkit.core.service.McuService
import com.snaggly.ksw_toolkit.gui.viewmodels.SystemTwaksViewModel

class SystemTwaks(mcuServiceObserver: LiveData<McuService?>) : FragmentMcuServiceView(mcuServiceObserver) {

    init {
        mcuServiceObserver.observe(this, { mcuServiceObj ->
            mcuService = mcuServiceObj
        })
    }

    companion object {
        fun newInstance(mcuServiceObserver: LiveData<McuService?>) = SystemTwaks(mcuServiceObserver)
    }

    private lateinit var viewModel: SystemTwaksViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.system_twaks_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SystemTwaksViewModel::class.java)
        // TODO: Use the ViewModel
    }

}