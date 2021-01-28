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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.system_twaks_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()
        startAtBoot.requestFocus()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SystemTwaksViewModel::class.java)
        viewModel.initConfig(requireContext())
        initElements()
        initButtonClickEvents()
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

        startAtBoot.isChecked = viewModel.config?.systemTweaks!!.startAtBoot.data
        stopKswServiceSwitch.isChecked = viewModel.config?.systemTweaks!!.kswService.data
        autoVolumeSwitch.isChecked = viewModel.config?.systemTweaks!!.autoVolume.data
        maxVolumeOnBootSwitch.isChecked = viewModel.config?.systemTweaks!!.maxVolume.data
        hideTopBarSwitch.isChecked = viewModel.config?.systemTweaks!!.hideTopBar.data
        shrinkTopBarSwitch.isChecked = viewModel.config?.systemTweaks!!.shrinkTopBar.data
        val actualDpi = requireView().resources.displayMetrics.densityDpi
        viewModel.config?.systemTweaks!!.dpi.data = actualDpi
        dpiInputField.setText(actualDpi.toString())
    }

    private fun initButtonClickEvents() {
        startAtBoot.setOnCheckedChangeListener { _, isChecked ->
            viewModel.config?.systemTweaks!!.startAtBoot.data = isChecked
        }

        autoVolumeSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.config?.systemTweaks!!.autoVolume.data = isChecked
        }

        maxVolumeOnBootSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.config?.systemTweaks!!.maxVolume.data = isChecked
        }

        stopKswServiceSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.config?.systemTweaks!!.kswService.data = isChecked

            if (!isChecked) {
                val alert = AlertDialog.Builder(requireContext(), R.style.alertDialogNight).setTitle("KSW-ToolKit-SystemTweaks")
                        .setMessage("You are about to shut down the built in KSW McuService. This could lead to unaccounted issues. Are you sure you still want to proceed?").create()
                alert.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                alert.setButton(AlertDialog.BUTTON_POSITIVE, "Yes!") { _, _ ->
                    viewModel.config?.systemTweaks!!.kswService.data = isChecked
                    val enableLoggingDialog = AlertDialog.Builder(requireContext(), R.style.alertDialogNight).setTitle("KSW-ToolKit-SystemTweaks")
                            .setMessage("Enable CarData logging for 3rd party Dashboard-Apps?").create()
                    enableLoggingDialog.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                    enableLoggingDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes") { _, _ ->
                        viewModel.config?.systemTweaks!!.carDataLogging.data = true
                        try {
                            viewModel.restartMcuReader()
                        } catch (exception: Exception) {
                            val alertExc = AlertDialog.Builder(context, R.style.alertDialogNight).setTitle("KSW-ToolKit-SystemTweaks")
                                    .setMessage("Could not restart McuReader!\n\n${exception.localizedMessage}").create()
                            alertExc.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                            alertExc.show()
                        }
                    }
                    enableLoggingDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No") { _, _ ->
                        viewModel.config?.systemTweaks!!.carDataLogging.data = false
                        try {
                            viewModel.restartMcuReader()
                        } catch (exception: Exception) {
                            val alertExc = AlertDialog.Builder(context, R.style.alertDialogNight).setTitle("KSW-ToolKit-SystemTweaks")
                                    .setMessage("Could not restart McuReader!\n\n${exception.localizedMessage}").create()
                            alertExc.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                            alertExc.show()
                        }

                    }
                    enableLoggingDialog.show()
                }
                alert.setButton(AlertDialog.BUTTON_NEGATIVE, "No") { _, _ ->
                    stopKswServiceSwitch.isChecked = true
                }
                alert.show()
            } else {
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
                viewModel.config?.systemTweaks!!.hideTopBar.data = isChecked
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
                viewModel.config?.systemTweaks!!.shrinkTopBar.data = isChecked
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
                viewModel.config?.systemTweaks?.dpi!!.data = newDPI
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
    }
}