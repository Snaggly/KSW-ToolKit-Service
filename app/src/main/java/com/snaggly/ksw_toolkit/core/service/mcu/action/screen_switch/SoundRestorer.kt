package com.snaggly.ksw_toolkit.core.service.mcu.action.screen_switch

import android.content.Context
import android.media.AudioManager
import android.os.Handler
import android.view.KeyEvent
import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import projekt.auto.mcu.ksw.serial.collection.McuCommands
import projekt.auto.mcu.ksw.serial.enums.SOUND_SRC_TYPE


class SoundRestorer(val context: Context) : IScreenSwitchAction {
    override fun performOnAndroidSwitch() {
        reEnableSound()
    }

    override fun performOnOEMSwitch() {
        reEnableSound()
        
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (am.isMusicActive) {
            Handler(context.mainLooper).postDelayed({
                am.dispatchMediaKeyEvent(
                    KeyEvent(
                        KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_MEDIA_PAUSE
                    )
                )
                am.dispatchMediaKeyEvent(
                    KeyEvent(
                        KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_MEDIA_PLAY
                    )
                )
            }, 1500)
        }
    }

    override fun restoreState() {

    }

    private fun reEnableSound() {
        McuLogic.mcuCommunicator?.sendCommand(McuCommands.SetMusicSource(SOUND_SRC_TYPE.SRC_ATSL_AIRCONSOLE))
    }
}