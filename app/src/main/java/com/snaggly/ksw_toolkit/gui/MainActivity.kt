package com.snaggly.ksw_toolkit.gui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.snaggly.ksw_toolkit.R
import com.snaggly.ksw_toolkit.core.service.McuService

class MainActivity : AppCompatActivity() {
    private lateinit var mcuServiceIntent: Intent
    private val soundRestorerFragment: Fragment = SoundRestorer()
    private val eventManagerFragment: Fragment = EventManager()
    private val systemTweaksFragment: Fragment = SystemTwaks()
    private val adbShellFragment: Fragment = AdbShell()
    private val mcuListenerFragment: Fragment = McuListener()
    private val configFragment: Fragment = Config()
    private lateinit var mFragManager: FragmentManager
    private lateinit var soundRestorerPane: Button
    private lateinit var eventManagerPane: Button
    private lateinit var systemTweaksPane: Button
    private lateinit var adbShellPane: Button
    private lateinit var mcuListenerPane: Button
    private lateinit var configImportExportPane: Button
    private lateinit var previousCallingButton: Button
    private var tabFragmentId = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!checkPermissions())
            finish()
    }

    override fun onStart() {
        super.onStart()
        mcuServiceIntent = Intent(this, McuService::class.java)
        startService(mcuServiceIntent)
    }

    override fun onResume() {
        super.onResume()
        startApp()
    }

    override fun onStop() {
        super.onStop()
        stopService(mcuServiceIntent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        checkPermissions()
    }

    private fun checkPermissions(): Boolean {
        if (!Settings.canDrawOverlays(this)) {
            startActivityForResult(Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse("package:$packageName")), 5469)
            return false
        }
        return true
    }

    private fun startApp() {
        setContentView(R.layout.activity_main)
        mFragManager = supportFragmentManager
        initViewElements()
        setBtnClicks()
        initPaneFragment()
    }

    private fun initViewElements() {
        soundRestorerPane = findViewById(R.id.soundRestorerPaneBtn)
        eventManagerPane = findViewById(R.id.eventManagerPaneBtn)
        systemTweaksPane = findViewById(R.id.systemTweaksPaneBtn)
        adbShellPane = findViewById(R.id.adbShellPaneBtn)
        mcuListenerPane = findViewById(R.id.mcuListenerPaneBtn)
        configImportExportPane = findViewById(R.id.configPaneBtn)
        tabFragmentId = R.id.tabFragment
    }

    private fun setBtnClicks() {
        soundRestorerPane.setOnClickListener { switchFragment(mFragManager, soundRestorerPane, soundRestorerFragment) }
        eventManagerPane.setOnClickListener { switchFragment(mFragManager, eventManagerPane, eventManagerFragment) }
        systemTweaksPane.setOnClickListener { switchFragment(mFragManager, systemTweaksPane, systemTweaksFragment) }
        adbShellPane.setOnClickListener { switchFragment(mFragManager, adbShellPane, adbShellFragment) }
        mcuListenerPane.setOnClickListener { switchFragment(mFragManager, mcuListenerPane, mcuListenerFragment) }
        configImportExportPane.setOnClickListener { switchFragment(mFragManager, configImportExportPane, configFragment) }
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