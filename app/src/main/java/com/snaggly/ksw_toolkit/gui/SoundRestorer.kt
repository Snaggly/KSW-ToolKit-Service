package com.snaggly.ksw_toolkit.gui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.snaggly.ksw_toolkit.R
import com.snaggly.ksw_toolkit.gui.viewmodels.SoundRestorerViewModel

class SoundRestorer : Fragment() {
    private var mViewModel: SoundRestorerViewModel? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.sound_restorer_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(SoundRestorerViewModel::class.java)
        // TODO: Use the ViewModel
    }

    companion object {
        fun newInstance(): SoundRestorer? {
            return SoundRestorer()
        }
    }
}