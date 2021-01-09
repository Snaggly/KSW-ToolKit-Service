package com.snaggly.ksw_toolkit.gui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.snaggly.ksw_toolkit.R
import com.snaggly.ksw_toolkit.core.service.McuService
import com.snaggly.ksw_toolkit.gui.viewmodels.SoundRestorerViewModel

class SoundRestorer(mcuServiceObserver: LiveData<McuService?>) : FragmentMcuServiceView(mcuServiceObserver) {

    init {
        mcuServiceObserver.observe(this, { mcuServiceObj ->
            mcuService = mcuServiceObj
        })
    }

    private var mViewModel: SoundRestorerViewModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.sound_restorer_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(SoundRestorerViewModel::class.java)
        val startOnBootBtn = requireView().findViewById<SwitchCompat>(R.id.startOnBootSwitch)
        startOnBootBtn.requestFocus()
        val initSourceBtn = requireView().findViewById<Button>(R.id.initSourceBtn)
        initSourceBtn.setOnClickListener { }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    companion object {
        fun newInstance(mcuService: LiveData<McuService?>) = SoundRestorer(mcuService)
    }
}
