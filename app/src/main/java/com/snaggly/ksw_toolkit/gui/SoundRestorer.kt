package com.snaggly.ksw_toolkit.gui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.snaggly.ksw_toolkit.R
import com.snaggly.ksw_toolkit.gui.viewmodels.SoundRestorerViewModel

class SoundRestorer : Fragment() {

    private lateinit var mViewModel: SoundRestorerViewModel

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
    }

    override fun onStop() {
        super.onStop()
    }

    companion object {
        fun newInstance() = SoundRestorer()
    }
}
