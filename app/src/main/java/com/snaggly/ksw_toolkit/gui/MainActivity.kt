package com.snaggly.ksw_toolkit.gui

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.snaggly.ksw_toolkit.R

class MainActivity : AppCompatActivity() {
    private val soundRestorerFragment: Fragment = SoundRestorer()
    private val eventManagerFragment: Fragment = EventManager()
    private lateinit var mFragManager: FragmentManager
    private lateinit var soundRestorerPane: Button
    private lateinit var eventManagerPane: Button
    private lateinit var systemTweaksPane: Button
    private lateinit var adbShellPane: Button
    private lateinit var mcuListenerPane: Button
    private lateinit var previousCallingButton: Button
    private var tabFragmentId = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mFragManager = supportFragmentManager
        initViewElements()
        setBtnClicks()
        initPaneFragment()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    private fun initViewElements() {
        soundRestorerPane = findViewById(R.id.soundRestorerPaneBtn)
        eventManagerPane = findViewById(R.id.eventManagerPaneBtn)
        systemTweaksPane = findViewById(R.id.systemTweaksPaneBtn)
        adbShellPane = findViewById(R.id.adbShellPaneBtn)
        mcuListenerPane = findViewById(R.id.mcuListenerPaneBtn)
        tabFragmentId = R.id.tabFragment
    }

    private fun setBtnClicks() {
        soundRestorerPane.setOnClickListener { switchFragment(mFragManager, soundRestorerPane, soundRestorerFragment) }
        eventManagerPane.setOnClickListener { switchFragment(mFragManager, eventManagerPane, eventManagerFragment) }
        systemTweaksPane.setOnClickListener { }
        adbShellPane.setOnClickListener { }
        mcuListenerPane.setOnClickListener { }
    }

    private fun initPaneFragment() {
        previousCallingButton = soundRestorerPane
        switchFragment(mFragManager, soundRestorerPane, soundRestorerFragment)
        soundRestorerPane.requestFocus()
    }

    private fun switchFragment(manager: FragmentManager, callingButton: Button, fragment: Fragment) {
        previousCallingButton.setBackgroundResource(R.drawable.btn_selector)
        callingButton.setBackgroundResource(R.drawable.ksw_id7_itme_select_bg_focused)
        previousCallingButton = callingButton
        manager.beginTransaction().replace(tabFragmentId, fragment).commit()
    }
}