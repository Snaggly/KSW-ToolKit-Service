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
import com.snaggly.ksw_toolkit.util.enums.EventManagerTypes

class EventManager : Fragment() {

    private lateinit var telefonBtn : Button
    private lateinit var telefonLongBtn : Button
    private lateinit var voiceBtn : Button
    private lateinit var mediaPreviousBtn : Button
    private lateinit var mediaNextBtn : Button
    private lateinit var mediaPauseBtn : Button
    private lateinit var modeBtn : Button
    private lateinit var knobPressBtn : Button
    private lateinit var knobTiltUpBtn : Button
    private lateinit var knobTiltDownBtn : Button
    private lateinit var knobTiltLeftBtn : Button
    private lateinit var knobTiltRightBtn : Button
    private lateinit var knobTurnLeftBtn : Button
    private lateinit var knobTurnRightBtn : Button
    private lateinit var menuBtn : Button
    private lateinit var backBtn : Button
    private lateinit var optionsBtn : Button
    private lateinit var naviBtn : Button

    private lateinit var activeFragment: EventManagerSelectAction

    private lateinit var mViewModel: EventManagerViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.event_manager_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(EventManagerViewModel::class.java)
        initBtns()
        initBtnClick()
        voiceBtn.requestFocus()

    }

    private fun initBtns() {
        telefonBtn = requireView().findViewById(R.id.telefonBtn)
        telefonLongBtn = requireView().findViewById(R.id.telefonLongPressBtn)
        voiceBtn = requireView().findViewById(R.id.voiceBtn)
        mediaPreviousBtn = requireView().findViewById(R.id.mediaPreviousBtn)
        mediaNextBtn = requireView().findViewById(R.id.mediaNextBtn)
        mediaPauseBtn = requireView().findViewById(R.id.mediaPauseBtn)
        modeBtn = requireView().findViewById(R.id.modeBtn)
        knobPressBtn = requireView().findViewById(R.id.knobPressBtn)
        knobTiltUpBtn = requireView().findViewById(R.id.knobTiltUpBtn)
        knobTiltDownBtn = requireView().findViewById(R.id.knobTiltDownBtn)
        knobTiltLeftBtn = requireView().findViewById(R.id.knobTiltLeftBtn)
        knobTiltRightBtn = requireView().findViewById(R.id.knobTiltRightBtn)
        knobTurnLeftBtn = requireView().findViewById(R.id.knobTurnLeftBtn)
        knobTurnRightBtn = requireView().findViewById(R.id.knobTurnRightBtn)
        menuBtn = requireView().findViewById(R.id.menuButtonBtn)
        backBtn = requireView().findViewById(R.id.backButtonBtn)
        optionsBtn = requireView().findViewById(R.id.optionsButtonBtn)
        naviBtn = requireView().findViewById(R.id.navigationButtonBtn)
    }

    private fun initBtnClick() {
        setOnClickEvent(telefonBtn, EventManagerTypes.TelephoneButton)
        setOnClickEvent(telefonLongBtn, EventManagerTypes.TelephoneButtonLongPress)
        setOnClickEvent(voiceBtn, EventManagerTypes.VoiceCommandButton)
        setOnClickEvent(mediaPreviousBtn, EventManagerTypes.MediaPrevious)
        setOnClickEvent(mediaNextBtn, EventManagerTypes.MediaNext)
        setOnClickEvent(mediaPauseBtn, EventManagerTypes.MediaPlayPause)
        setOnClickEvent(modeBtn, EventManagerTypes.ModeButton)
        setOnClickEvent(knobPressBtn, EventManagerTypes.KnobPress)
        setOnClickEvent(knobTiltUpBtn, EventManagerTypes.KnobTiltUp)
        setOnClickEvent(knobTiltDownBtn, EventManagerTypes.KnobTiltDown)
        setOnClickEvent(knobTiltLeftBtn, EventManagerTypes.KnobTiltLeft)
        setOnClickEvent(knobTiltRightBtn, EventManagerTypes.KnobTiltRight)
        setOnClickEvent(knobTurnLeftBtn, EventManagerTypes.KnobTurnLeft)
        setOnClickEvent(knobTurnRightBtn, EventManagerTypes.KnobTurnRight)
        setOnClickEvent(menuBtn, EventManagerTypes.MenuButton)
        setOnClickEvent(backBtn, EventManagerTypes.BackButton)
        setOnClickEvent(optionsBtn, EventManagerTypes.OptionsButton)
        setOnClickEvent(naviBtn, EventManagerTypes.NavigationButton)
    }

    private fun setOnClickEvent(button: Button, types: EventManagerTypes) {
        button.setOnClickListener {
            activeFragment = EventManagerSelectAction(types, object : OnActionResult {
                override fun notifyView() {
                    button.text = getString(R.string.assigned)
                    childFragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.slide_enter_right_left, R.anim.slide_exit_right_left)
                            .remove(activeFragment)
                            .commit()
                }
            })
            button.text = "..."
            childFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_enter_right_left, R.anim.slide_exit_right_left)
                    .replace(R.id.eventManagerSelectActionFrame, activeFragment)
                    .commit()
        }
    }

    companion object {
        fun newInstance() = EventManager()
    }

    interface OnActionResult{
        fun notifyView()
    }
}