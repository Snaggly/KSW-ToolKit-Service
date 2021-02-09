package com.snaggly.ksw_toolkit.gui

import android.app.AlertDialog
import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
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
    private lateinit var autoVolumeSwitch: SwitchCompat
    private lateinit var maxVolumeOnBootSwitch: SwitchCompat
    private lateinit var changeDPIButton: Button
    private lateinit var giveTaskerLogcatPermBtn: Button
    private lateinit var dpiInputField: TextInputEditText
    private lateinit var logCarDataToggle: SwitchCompat
    private lateinit var logMcuEventsToggle: SwitchCompat
    private lateinit var muxNaviCalloutsToggle: SwitchCompat

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
        autoVolumeSwitch = requireView().findViewById(R.id.autoVolumeToggle)
        maxVolumeOnBootSwitch = requireView().findViewById(R.id.maxVolumeAtBootToggle)
        hideTopBarSwitch = requireView().findViewById(R.id.hideTopBarToggle)
        shrinkTopBarSwitch = requireView().findViewById(R.id.shrinkTopBarToggle)
        changeDPIButton = requireView().findViewById(R.id.changeDpiBtn)
        giveTaskerLogcatPermBtn = requireView().findViewById(R.id.giveTaskerLogcat)
        dpiInputField = requireView().findViewById(R.id.dpiTxtInput)
        logCarDataToggle = requireView().findViewById(R.id.logCarDataToggle)
        logMcuEventsToggle = requireView().findViewById(R.id.logMcuEventsToggle)
        muxNaviCalloutsToggle = requireView().findViewById(R.id.muxNaviCalloutsToggle)

        startAtBoot.isChecked = viewModel.getConfig(requireContext()).startAtBoot.data
        stopKswServiceSwitch.isChecked = viewModel.getConfig(requireContext()).kswService.data
        autoVolumeSwitch.isChecked = viewModel.getConfig(requireContext()).autoVolume.data
        maxVolumeOnBootSwitch.isChecked = viewModel.getConfig(requireContext()).maxVolume.data
        hideTopBarSwitch.isChecked = viewModel.getConfig(requireContext()).hideTopBar.data
        shrinkTopBarSwitch.isChecked = viewModel.getConfig(requireContext()).shrinkTopBar.data
        val actualDpi = requireView().resources.displayMetrics.densityDpi
        viewModel.getConfig(requireContext()).dpi.data = actualDpi
        dpiInputField.setText(actualDpi.toString())
        logCarDataToggle.isChecked = viewModel.getConfig(requireContext()).carDataLogging.data
        logMcuEventsToggle.isChecked = viewModel.getConfig(requireContext()).logMcuEvent.data
        muxNaviCalloutsToggle.isChecked = viewModel.getConfig(requireContext()).muxNaviVoice.data
        if (!viewModel.getConfig(requireContext()).kswService.data) {
            logCarDataToggle.isEnabled = true
            logMcuEventsToggle.isEnabled = true
            muxNaviCalloutsToggle.isEnabled = true
        }
    }

    private fun initButtonClickEvents() {
        startAtBoot.setOnCheckedChangeListener { _, isChecked ->
            viewModel.getConfig(requireContext()).startAtBoot.data = isChecked
        }

        autoVolumeSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.getConfig(requireContext()).autoVolume.data = isChecked
            try {
                viewModel.restartMcuReader()
            } catch (exception: Exception) {
                val alertExc = AlertDialog.Builder(context, R.style.alertDialogNight).setTitle("KSW-ToolKit-SystemTweaks")
                        .setMessage("Could not restart McuReader!\n\n${exception.localizedMessage}").create()
                alertExc.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                alertExc.show()
            }
        }

        maxVolumeOnBootSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.getConfig(requireContext()).maxVolume.data = isChecked
        }

        stopKswServiceSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.getConfig(requireContext()).kswService.data = isChecked

            if (!isChecked) {
                val alert = AlertDialog.Builder(requireContext(), R.style.alertDialogNight).setTitle("KSW-ToolKit-SystemTweaks")
                        .setMessage("You are about to shut down the built in KSW McuService. This could lead to unaccounted issues. Are you sure you still want to proceed?").create()
                alert.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                alert.setButton(AlertDialog.BUTTON_POSITIVE, "Yes!") { _, _ ->
                    logCarDataToggle.isEnabled = true
                    logMcuEventsToggle.isEnabled = true
                    muxNaviCalloutsToggle.isEnabled = true
                    try {
                        viewModel.restartMcuReader()
                    } catch (exception: Exception) {
                        val alertExc = AlertDialog.Builder(context, R.style.alertDialogNight).setTitle("KSW-ToolKit-SystemTweaks")
                                .setMessage("Could not restart McuReader!\n\n${exception.localizedMessage}").create()
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
                muxNaviCalloutsToggle.isChecked = false
                logCarDataToggle.isEnabled = false
                logMcuEventsToggle.isEnabled = false
                muxNaviCalloutsToggle.isEnabled = false
                try {
                    viewModel.restartMcuReader()
                } catch (exception: Exception) {
                    val alert = AlertDialog.Builder(context, R.style.alertDialogNight).setTitle("KSW-ToolKit-SystemTweaks")
                            .setMessage("Could not restart McuReader!\n\n${exception.localizedMessage}").create()
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
                viewModel.getConfig(requireContext()).hideTopBar.data = isChecked
            } catch (exception: Exception) {
                val alert = AlertDialog.Builder(context, R.style.alertDialogNight).setTitle("KSW-ToolKit-SystemTweaks")
                        .setMessage("Unable to mess with TopBar!\n\n${exception.localizedMessage}").create()
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
                viewModel.getConfig(requireContext()).shrinkTopBar.data = isChecked
            } catch (exception: Exception) {
                val alert = AlertDialog.Builder(context, R.style.alertDialogNight).setTitle("KSW-ToolKit-SystemTweaks")
                        .setMessage("Unable to mess with TopBar!\n\n${exception.localizedMessage}").create()
                alert.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                alert.show()
            }
        }

        changeDPIButton.setOnClickListener {
            try {
                val newDPI = Integer.parseInt(dpiInputField.text.toString())
                viewModel.getConfig(requireContext()).dpi.data = newDPI
                viewModel.changeDPI(newDPI)
            } catch (exception: Exception) {
                val alert = AlertDialog.Builder(context, R.style.alertDialogNight).setTitle("KSW-ToolKit-SystemTweaks")
                        .setMessage("Exception Saving DPI!\n\n${exception.localizedMessage}").create()
                alert.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                alert.show()
            }
        }

        giveTaskerLogcatPermBtn.setOnClickListener {
            try {
                viewModel.coreService?.adbConnection!!.sendCommand("pm grant net.dinglisch.android.taskerm android.permission.READ_LOGS")
            } catch (exception: Exception) {
                val alert = AlertDialog.Builder(context, R.style.alertDialogNight).setTitle("KSW-ToolKit-SystemTweaks")
                        .setMessage("Unable to give Tasker Logcat Permission!\n\n${exception.localizedMessage}").create()
                alert.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                alert.show()
            }
        }

        logCarDataToggle.setOnCheckedChangeListener { _, isChecked ->
            viewModel.getConfig(requireContext()).carDataLogging.data = isChecked
            try {
                viewModel.restartMcuReader()
            } catch (exception: Exception) {
                val alertExc = AlertDialog.Builder(context, R.style.alertDialogNight).setTitle("KSW-ToolKit-SystemTweaks")
                        .setMessage("Could not restart McuReader!\n\n${exception.localizedMessage}").create()
                alertExc.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                alertExc.show()
            }
        }

        logMcuEventsToggle.setOnCheckedChangeListener { _, isChecked ->
            viewModel.getConfig(requireContext()).logMcuEvent.data = isChecked
        }

        muxNaviCalloutsToggle.setOnCheckedChangeListener { _, isChecked ->
            viewModel.getConfig(requireContext()).muxNaviVoice.data = isChecked
            if (isChecked)
                viewModel.startVoice()
            else
                viewModel.stopVoice()
        }
    }
}