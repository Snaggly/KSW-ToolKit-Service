package com.snaggly.ksw_toolkit.gui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.snaggly.ksw_toolkit.R
import com.snaggly.ksw_toolkit.core.service.McuService
import com.snaggly.ksw_toolkit.gui.viewmodels.SoundRestorerViewModel

class SoundRestorer : Fragment() {
    private var mViewModel: SoundRestorerViewModel? = null
    private var isBound = false
    private lateinit var mcuService: McuService

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            mcuService = (service as McuService.McuServiceBinder).getService()
            isBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isBound = false
        }
    }


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
        initSourceBtn.setOnClickListener {
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(requireContext(), McuService::class.java).also { intent ->
            requireActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        requireActivity().unbindService(connection)
    }

    companion object {
        fun newInstance(): SoundRestorer? {
            return SoundRestorer()
        }
    }
}