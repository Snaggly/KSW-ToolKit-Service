package com.snaggly.ksw_toolkit.gui

import android.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.SeekBar
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.snaggly.ksw_toolkit.R
import com.snaggly.ksw_toolkit.gui.viewmodels.SystemTwaksViewModel

class SystemTwaks : Fragment() {

    companion object {
        fun newInstance() = SystemTwaks()
    }

    private lateinit var viewModel: SystemTwaksViewModel
    private lateinit var startAtBoot: SwitchCompat
    private lateinit var stopKswServiceSwitch: SwitchCompat
    private lateinit var hideTopBarSwitch: SwitchCompat
    private lateinit var shrinkTopBarSwitch: SwitchCompat
    private lateinit var autoThemeToggle: SwitchCompat
    private lateinit var autoVolumeSwitch: SwitchCompat
    private lateinit var maxVolumeOnBootSwitch: SwitchCompat
    private lateinit var giveTaskerLogcatPermBtn: Button
    private lateinit var logCarDataToggle: SwitchCompat
    private lateinit var logMcuEventsToggle: SwitchCompat
    private lateinit var interceptMcuCommandToggle: SwitchCompat
    private lateinit var extraBtnHandleToggle: SwitchCompat
    private lateinit var nightBrightnessToggle: SwitchCompat
    private lateinit var nightBrightnessSeekBar: SeekBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.system_twaks_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()
        initElements()
        initButtonClickEvents()
        startAtBoot.requestFocus()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SystemTwaksViewModel::class.java)
    }

    private fun initElements() {
        startAtBoot = requireView().findViewById(R.id.startAppAtBootTgl)
        stopKswServiceSwitch = requireView().findViewById(R.id.startStopKSWServiceSwitch)
        autoThemeToggle = requireView().findViewById(R.id.autoThemeToggle)
        autoVolumeSwitch = requireView().findViewById(R.id.autoVolumeToggle)
        maxVolumeOnBootSwitch = requireView().findViewById(R.id.maxVolumeAtBootToggle)
        hideTopBarSwitch = requireView().findViewById(R.id.hideTopBarToggle)
        shrinkTopBarSwitch = requireView().findViewById(R.id.shrinkTopBarToggle)
        giveTaskerLogcatPermBtn = requireView().findViewById(R.id.giveTaskerLogcat)
        logCarDataToggle = requireView().findViewById(R.id.logCarDataToggle)
        logMcuEventsToggle = requireView().findViewById(R.id.logMcuEventsToggle)
        interceptMcuCommandToggle = requireView().findViewById(R.id.interceptMcuCommandsToggle)
        extraBtnHandleToggle = requireView().findViewById(R.id.extraBtnHandleToggle)
        nightBrightnessToggle = requireView().findViewById(R.id.nightBrightnessToggle)
        nightBrightnessSeekBar = requireView().findViewById(R.id.nightBrightnessSeekBar)

        startAtBoot.isChecked = viewModel.getConfig().startAtBoot.data
        stopKswServiceSwitch.isChecked = viewModel.getConfig().kswService.data
        autoThemeToggle.isChecked = viewModel.getConfig().autoTheme.data
        autoVolumeSwitch.isChecked = viewModel.getConfig().autoVolume.data
        maxVolumeOnBootSwitch.isChecked = viewModel.getConfig().maxVolume.data
        hideTopBarSwitch.isChecked = viewModel.getConfig().hideTopBar.data
        shrinkTopBarSwitch.isChecked = viewModel.getConfig().shrinkTopBar.data
        logCarDataToggle.isChecked = viewModel.getConfig().carDataLogging.data
        logMcuEventsToggle.isChecked = viewModel.getConfig().logMcuEvent.data
        interceptMcuCommandToggle.isChecked = viewModel.getConfig().interceptMcuCommand.data
        extraBtnHandleToggle.isChecked = viewModel.getConfig().extraMediaButtonHandle.data
        nightBrightnessToggle.isChecked = viewModel.getConfig().nightBrightness.data
        nightBrightnessSeekBar.progress = viewModel.getConfig().nightBrightnessLevel.data
        if (!viewModel.getConfig().kswService.data) {
            logCarDataToggle.isEnabled = true
            logMcuEventsToggle.isEnabled = true
            interceptMcuCommandToggle.isEnabled = true
        }
        if (!nightBrightnessToggle.isChecked) {
            nightBrightnessSeekBar.isEnabled = false
        }
    }

    private fun initButtonClickEvents() {
        startAtBoot.setOnCheckedChangeListener { _, isChecked ->
            viewModel.getConfig().startAtBoot.data = isChecked
        }

        autoThemeToggle.setOnCheckedChangeListener { _, isChecked ->
            viewModel.getConfig().autoTheme.data = isChecked
            try {
                viewModel.restartMcuReader()
            } catch (exception: Exception) {
                val alertExc = AlertDialog.Builder(context, R.style.alertDialogNight).setTitle("KSW-ToolKit-SystemTweaks")
                        .setMessage("Could not restart McuReader!\n\n${exception.stackTrace}").create()
                alertExc.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                alertExc.show()
            }
        }

        autoVolumeSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.getConfig().autoVolume.data = isChecked
            try {
                viewModel.restartMcuReader()
            } catch (exception: Exception) {
                val alertExc = AlertDialog.Builder(context, R.style.alertDialogNight).setTitle("KSW-ToolKit-SystemTweaks")
                        .setMessage("Could not restart McuReader!\n\n${exception.stackTrace}").create()
                alertExc.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                alertExc.show()
            }
        }

        maxVolumeOnBootSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.getConfig().maxVolume.data = isChecked
        }

        stopKswServiceSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.getConfig().kswService.data = isChecked

            if (!isChecked) {
                val alert = AlertDialog.Builder(requireContext(), R.style.alertDialogNight).setTitle("KSW-ToolKit-SystemTweaks")
                        .setMessage("You are about to shut down the built in KSW McuService. This could lead to unaccounted issues. Are you sure you still want to proceed?").create()
                alert.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                alert.setButton(AlertDialog.BUTTON_POSITIVE, "Yes!") { _, _ ->
                    logCarDataToggle.isEnabled = true
                    logMcuEventsToggle.isEnabled = true
                    interceptMcuCommandToggle.isEnabled = true
                    try {
                        viewModel.restartMcuReader()
                    } catch (exception: Exception) {
                        val alertExc = AlertDialog.Builder(context, R.style.alertDialogNight).setTitle("KSW-ToolKit-SystemTweaks")
                                .setMessage("Could not restart McuReader!\n\n${exception.stackTrace}").create()
                        alertExc.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                        alertExc.show()
                    }
                }
                alert.setButton(AlertDialog.BUTTON_NEGATIVE, "No") { _, _ ->
                    stopKswServiceSwitch.isChecked = true
                }
                alert.show()
            } else {
                logCarDataToggle.isChecked = false
                logMcuEventsToggle.isChecked = false
                interceptMcuCommandToggle.isChecked = false
                logCarDataToggle.isEnabled = false
                logMcuEventsToggle.isEnabled = false
                interceptMcuCommandToggle.isEnabled = false
                try {
                    viewModel.restartMcuReader()
                } catch (exception: Exception) {
                    val alert = AlertDialog.Builder(context, R.style.alertDialogNight).setTitle("KSW-ToolKit-SystemTweaks")
                            .setMessage("Could not restart McuReader!\n\n${exception.stackTrace}").create()
                    alert.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                    alert.show()
                }
            }

        }

        hideTopBarSwitch.setOnCheckedChangeListener { _, isChecked ->
            try {
                if (isChecked) {
                    shrinkTopBarSwitch.isChecked = false
                    viewModel.hideTopBar()
                } else {
                    viewModel.showTopBar()
                }
                viewModel.getConfig().hideTopBar.data = isChecked
            } catch (exception: Exception) {
                val alert = AlertDialog.Builder(context, R.style.alertDialogNight).setTitle("KSW-ToolKit-SystemTweaks")
                        .setMessage("Unable to mess with TopBar!\n\n${exception.stackTrace}").create()
                alert.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                alert.show()
            }
        }

        shrinkTopBarSwitch.setOnCheckedChangeListener { _, isChecked ->
            try {
                if (isChecked) {
                    hideTopBarSwitch.isChecked = false
                    viewModel.shrinkTopBar()
                } else {
                    viewModel.restoreTopBar()
                }
                viewModel.getConfig().shrinkTopBar.data = isChecked
            } catch (exception: Exception) {
                val alert = AlertDialog.Builder(context, R.style.alertDialogNight).setTitle("KSW-ToolKit-SystemTweaks")
                        .setMessage("Unable to mess with TopBar!\n\n${exception.stackTrace}").create()
                alert.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                alert.show()
            }
        }

        giveTaskerLogcatPermBtn.setOnClickListener {
            try {
                viewModel.giveTaskerPerm()
            } catch (exception: Exception) {
                val alert = AlertDialog.Builder(context, R.style.alertDialogNight).setTitle("KSW-ToolKit-SystemTweaks")
                        .setMessage("Unable to give Tasker Logcat Permission!\n\n${exception.stackTrace}").create()
                alert.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                alert.show()
            }
        }

        logCarDataToggle.setOnCheckedChangeListener { _, isChecked ->
            viewModel.getConfig().carDataLogging.data = isChecked
            try {
                viewModel.restartMcuReader()
            } catch (exception: Exception) {
                val alertExc = AlertDialog.Builder(context, R.style.alertDialogNight).setTitle("KSW-ToolKit-SystemTweaks")
                        .setMessage("Could not restart McuReader!\n\n${exception.stackTrace}").create()
                alertExc.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                alertExc.show()
            }
        }

        logMcuEventsToggle.setOnCheckedChangeListener { _, isChecked ->
            viewModel.getConfig().logMcuEvent.data = isChecked
            try {
                viewModel.restartMcuReader()
            } catch (exception: Exception) {
                val alertExc = AlertDialog.Builder(context, R.style.alertDialogNight).setTitle("KSW-ToolKit-SystemTweaks")
                        .setMessage("Could not restart McuReader!\n\n${exception.stackTrace}").create()
                alertExc.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                alertExc.show()
            }
        }

        interceptMcuCommandToggle.setOnCheckedChangeListener { _, isChecked ->
            viewModel.getConfig().interceptMcuCommand.data = isChecked

            try {
                viewModel.restartMcuReader()
            } catch (exception: Exception) {
                val alertExc = AlertDialog.Builder(context, R.style.alertDialogNight).setTitle("KSW-ToolKit-SystemTweaks")
                        .setMessage("Could not restart McuReader!\n\n${exception.stackTrace}").create()
                alertExc.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                alertExc.show()
            }
        }

        extraBtnHandleToggle.setOnCheckedChangeListener { _, isChecked ->
            viewModel.getConfig().extraMediaButtonHandle.data = isChecked

            try {
                viewModel.restartMcuReader()
            } catch (exception: Exception) {
                val alertExc = AlertDialog.Builder(context, R.style.alertDialogNight).setTitle("KSW-ToolKit-SystemTweaks")
                    .setMessage("Could not restart McuReader!\n\n${exception.stackTrace}").create()
                alertExc.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                alertExc.show()
            }
        }

        nightBrightnessToggle.setOnCheckedChangeListener { _, isChecked ->
            viewModel.getConfig().nightBrightness.data = isChecked
            nightBrightnessSeekBar.isEnabled = isChecked

            try {
                viewModel.restartMcuReader()
            } catch (exception: Exception) {
                val alertExc = AlertDialog.Builder(context, R.style.alertDialogNight).setTitle("KSW-ToolKit-SystemTweaks")
                    .setMessage("Could not restart McuReader!\n\n${exception.stackTrace}").create()
                alertExc.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                alertExc.show()
            }
        }

        nightBrightnessSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewModel.getConfig().nightBrightnessLevel.data = progress
                try {
                    viewModel.restartMcuReader()
                } catch (exception: Exception) {
                    val alertExc = AlertDialog.Builder(context, R.style.alertDialogNight).setTitle("KSW-ToolKit")
                        .setMessage("Could not restart McuReader!\n\n${exception.stackTrace}").create()
                    alertExc.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                    alertExc.show()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }
}