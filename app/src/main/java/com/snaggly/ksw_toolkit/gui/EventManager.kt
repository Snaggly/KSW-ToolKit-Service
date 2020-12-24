package com.snaggly.ksw_toolkit.gui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.snaggly.ksw_toolkit.R
import com.snaggly.ksw_toolkit.gui.viewmodels.EventManagerViewModel

class EventManager : Fragment() {
    private lateinit var mViewModel: EventManagerViewModel
    private val selectActionFragment: EventManagerSelectAction = EventManagerSelectAction("Empty")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.event_manager_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(EventManagerViewModel::class.java)
        // TODO: Use the ViewModel
        val telefonBtn = requireView().findViewById<Button?>(R.id.voiceBtn)
        telefonBtn.requestFocus()
        telefonBtn.setOnClickListener { v: View? ->
            telefonBtn.text = "..."
            childFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_enter_right_left, R.anim.slide_exit_right_left)
                    .replace(R.id.eventManagerSelectActionFrame, selectActionFragment)
                    .commit()
        }
    }

    companion object {
        fun newInstance(): EventManager? {
            return EventManager()
        }
    }
}