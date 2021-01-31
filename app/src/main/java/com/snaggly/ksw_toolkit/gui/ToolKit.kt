package com.snaggly.ksw_toolkit.gui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.snaggly.ksw_toolkit.R
import com.snaggly.ksw_toolkit.gui.viewmodels.ToolKitViewModel

class ToolKit : Fragment() {

    private lateinit var mViewModel: ToolKitViewModel
    private lateinit var carInfo: LinearLayout
    private lateinit var radio: LinearLayout
    private lateinit var frontCam: LinearLayout
    private lateinit var aux: LinearLayout
    private lateinit var dvr: LinearLayout
    private lateinit var dvd: LinearLayout
    private lateinit var dtv: LinearLayout
    private lateinit var brightnessBar: SeekBar
    private lateinit var screenOffBtn: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.toolkit_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(ToolKitViewModel::class.java)
        initViews()
        initClickEvents()
    }

    private fun initViews() {
        carInfo = requireView().findViewById(R.id.carInfoBtn)
        radio = requireView().findViewById(R.id.radioBtn)
        frontCam = requireView().findViewById(R.id.frontCamBtn)
        aux = requireView().findViewById(R.id.auxBtn)
        dvr = requireView().findViewById(R.id.dvrBtn)
        dvd = requireView().findViewById(R.id.dvdBtn)
        dtv = requireView().findViewById(R.id.dtvBtn)
        brightnessBar = requireView().findViewById(R.id.brightnessSeekBar)
        brightnessBar.progress = mViewModel.getBrightness(requireContext())
        screenOffBtn = requireView().findViewById(R.id.screenOffBtn)
        carInfo.requestFocus()
    }

    private fun initClickEvents() {
        brightnessBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                try {
                    mViewModel.setBrightness(requireContext(), progress)
                } catch (exception: Exception) {
                    val alertExc = AlertDialog.Builder(context, R.style.alertDialogNight).setTitle("KSW-ToolKit")
                            .setMessage("Unable to change System Brightness!\n\n${exception.localizedMessage}").create()
                    alertExc.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                    alertExc.show()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

        carInfo.setOnClickListener {
            try {
                mViewModel.openOEMScreen()
            } catch (exception: Exception) {
                val alertExc = AlertDialog.Builder(context, R.style.alertDialogNight).setTitle("KSW-ToolKit")
                        .setMessage("Unable to open OEM Screen!\n\n${exception.localizedMessage}").create()
                alertExc.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                alertExc.show()
            }
        }

        radio.setOnClickListener {
            try {
                mViewModel.openRadioScreen()
            } catch (exception: Exception) {
                val alertExc = AlertDialog.Builder(context, R.style.alertDialogNight).setTitle("KSW-ToolKit")
                        .setMessage("Unable to open Radio Screen!\n\n${exception.localizedMessage}").create()
                alertExc.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                alertExc.show()
            }
        }

        frontCam.setOnClickListener {
            try {
                mViewModel.openFCamScreen()
            } catch (exception: Exception) {
                val alertExc = AlertDialog.Builder(context, R.style.alertDialogNight).setTitle("KSW-ToolKit")
                        .setMessage("Unable to open F_CAM Screen!\n\n${exception.localizedMessage}").create()
                alertExc.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                alertExc.show()
            }
        }

        aux.setOnClickListener {
            try {
                mViewModel.openAuxScreen()
            } catch (exception: Exception) {
                val alertExc = AlertDialog.Builder(context, R.style.alertDialogNight).setTitle("KSW-ToolKit")
                        .setMessage("Unable to open AUX Screen!\n\n${exception.localizedMessage}").create()
                alertExc.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                alertExc.show()
            }
        }

        dvr.setOnClickListener {
            try {
                mViewModel.openDvrScreen()
            } catch (exception: Exception) {
                val alertExc = AlertDialog.Builder(context, R.style.alertDialogNight).setTitle("KSW-ToolKit")
                        .setMessage("Unable to open DVR Screen!\n\n${exception.localizedMessage}").create()
                alertExc.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                alertExc.show()
            }
        }

        dvd.setOnClickListener {
            try {
                mViewModel.openDvdScreen()
            } catch (exception: Exception) {
                val alertExc = AlertDialog.Builder(context, R.style.alertDialogNight).setTitle("KSW-ToolKit")
                        .setMessage("Unable to open DVD Screen!\n\n${exception.localizedMessage}").create()
                alertExc.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                alertExc.show()
            }
        }

        dtv.setOnClickListener {
            try {
                mViewModel.openDtvScreen()
            } catch (exception: Exception) {
                val alertExc = AlertDialog.Builder(context, R.style.alertDialogNight).setTitle("KSW-ToolKit")
                        .setMessage("Unable to open DTV Screen!\n\n${exception.localizedMessage}").create()
                alertExc.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                alertExc.show()
            }
        }

        screenOffBtn.setOnClickListener {
            try {
                mViewModel.closeScreen()
            } catch (exception: Exception) {
                val alertExc = AlertDialog.Builder(context, R.style.alertDialogNight).setTitle("KSW-ToolKit")
                        .setMessage("Unable to close screen!\n\n${exception.localizedMessage}").create()
                alertExc.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                alertExc.show()
            }
        }
    }

    companion object {
        fun newInstance() = ToolKit()
    }
}
