package com.snaggly.ksw_toolkit.gui

import android.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
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
        stopKswServiceSwitch = requireView().findViewById(R.id.startStopKSWServiceSwitch)
        startAtBoot = requireView().findViewById(R.id.startAppAtBootTgl)
        hideTopBarSwitch = requireView().findViewById(R.id.hideTopBarToggle)
        shrinkTopBarSwitch = requireView().findViewById(R.id.shrinkTopBarToggle)
        autoVolumeSwitch = requireView().findViewById(R.id.autoVolumeToggle)
        maxVolumeOnBootSwitch = requireView().findViewById(R.id.maxVolumeAtBootToggle)
        changeDPIButton = requireView().findViewById(R.id.changeDpiBtn)
        giveTaskerLogcatPermBtn = requireView().findViewById(R.id.giveTaskerLogcat)
        dpiInputField = requireView().findViewById(R.id.dpiTxtInput)

        stopKswServiceSwitch.isChecked = viewModel.config?.mcuListener!!.enableKsw.data
    }

    private fun initButtonClickEvents() {
        stopKswServiceSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                val alert = AlertDialog.Builder(requireContext()).setTitle("KSW-ToolKit-McuListener")
                        .setMessage("You are about to shut down the built in KSW McuService. This could lead to unaccounted issues. Are you sure you still want to proceed?").create()
                alert.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                alert.setButton(AlertDialog.BUTTON_POSITIVE, "Yes!") { _, _ ->
                    viewModel.restartMcuReader(isChecked, requireContext())
                }
                alert.setButton(AlertDialog.BUTTON_NEGATIVE, "No") { _, _ ->
                    stopKswServiceSwitch.isChecked = true
                }
                alert.show()
            } else {
                viewModel.restartMcuReader(isChecked, requireContext())
            }
        }
    }
}