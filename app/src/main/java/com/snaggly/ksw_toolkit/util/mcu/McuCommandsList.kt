package com.snaggly.ksw_toolkit.util.mcu

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.snaggly.ksw_toolkit.R
import com.snaggly.ksw_toolkit.util.ListType

class McuCommandsList(name: String, icon: Drawable, command: McuCommands) : ListType(name, icon) {
    companion object {
        fun getMcuCommandsList(context: Context) : ArrayList<McuCommandsList> {
            val result = ArrayList<McuCommandsList>()

            result.add(McuCommandsList("Turn Screen Off", ContextCompat.getDrawable(context,R.drawable.ic_baseline_web_asset_off_64)!!, McuCommands.ScreenOff))
            result.add(McuCommandsList("Increase Brightness", ContextCompat.getDrawable(context,R.drawable.ic_baseline_brightness_high_64)!!, McuCommands.BrightnessInc))
            result.add(McuCommandsList("Decrease Brightness", ContextCompat.getDrawable(context,R.drawable.ic_baseline_brightness_low_64)!!, McuCommands.BrightnessDec))
            result.add(McuCommandsList("Switch to OEM", ContextCompat.getDrawable(context,R.drawable.nbt_src_icon_carinfo)!!, McuCommands.CarInfo))
            result.add(McuCommandsList("Open Radio", ContextCompat.getDrawable(context,R.drawable.ic_baseline_radio_64)!!, McuCommands.Radio))
            result.add(McuCommandsList("Open Front Cam", ContextCompat.getDrawable(context,R.drawable.ic_baseline_camera_alt_64)!!, McuCommands.F_CAM))
            result.add(McuCommandsList("Open AUX", ContextCompat.getDrawable(context,R.drawable.ic_baseline_cable_64)!!, McuCommands.AUX))
            result.add(McuCommandsList("Open DVR", ContextCompat.getDrawable(context,R.drawable.ic_baseline_videocam_64)!!, McuCommands.DVR))
            result.add(McuCommandsList("Open DVD", ContextCompat.getDrawable(context,R.drawable.ic_baseline_settings_input_component_64)!!, McuCommands.DVD))
            result.add(McuCommandsList("Open DTV", ContextCompat.getDrawable(context,R.drawable.ic_baseline_live_tv_64)!!, McuCommands.DTV))

            return result
        }
    }
}