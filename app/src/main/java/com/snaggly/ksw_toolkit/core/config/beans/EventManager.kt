package com.snaggly.ksw_toolkit.core.config.beans

import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes
import com.snaggly.ksw_toolkit.util.list.eventtype.EventMode
import com.snaggly.ksw_toolkit.util.list.keyevent.KeyCode
import com.snaggly.ksw_toolkit.util.list.mcu.McuCommandsEnum

class EventManager(var eventMode: EventMode?, var keyCode: Int?, var appName: String?,
                   var mcuCommandMode: Int?, var taskerTaskName: String?
){

    companion object {

        fun initialButtons(): HashMap<EventManagerTypes, EventManager> {
            val result = hashMapOf<EventManagerTypes, EventManager>()
            for (type in EventManagerTypes.values()) {
                result[type] = EventManager(EventMode.NoAssignment, -1, "", -1, "")
            }

            return result
        }

        fun initStandardButtons(): HashMap<EventManagerTypes, EventManager> {
            return hashMapOf(
                EventManagerTypes.KnobPress to EventManager(
                    EventMode.KeyEvent,
                    KeyCode.ENTER.keycode,
                    "",
                    -1,
                    ""
                ),
                EventManagerTypes.KnobTiltDown to EventManager(
                    EventMode.KeyEvent,
                    KeyCode.DPAD_DOWN.keycode,
                    "",
                    -1,
                    ""
                ),
                EventManagerTypes.KnobTiltUp to EventManager(
                    EventMode.KeyEvent,
                    KeyCode.DPAD_UP.keycode,
                    "",
                    -1,
                    ""
                ),
                EventManagerTypes.KnobTiltLeft to EventManager(
                    EventMode.KeyEvent,
                    KeyCode.DPAD_LEFT.keycode,
                    "",
                    -1,
                    ""
                ),
                EventManagerTypes.KnobTiltRight to EventManager(
                    EventMode.KeyEvent,
                    KeyCode.DPAD_RIGHT.keycode,
                    "",
                    -1,
                    ""
                ),
                EventManagerTypes.KnobTurnLeft to EventManager(
                    EventMode.KeyEvent,
                    KeyCode.DPAD_UP.keycode,
                    "",
                    -1,
                    ""
                ),
                EventManagerTypes.KnobTurnRight to EventManager(
                    EventMode.KeyEvent,
                    KeyCode.DPAD_DOWN.keycode,
                    "",
                    -1,
                    ""
                ),
                EventManagerTypes.BackButton to EventManager(
                    EventMode.KeyEvent,
                    KeyCode.BACK.keycode,
                    "",
                    -1,
                    ""
                ),
                EventManagerTypes.MenuButton to EventManager(
                    EventMode.KeyEvent,
                    KeyCode.HOME.keycode,
                    "",
                    -1,
                    ""
                ),
                EventManagerTypes.OptionsButton to EventManager(
                    EventMode.McuCommand,
                    -1,
                    "",
                    McuCommandsEnum.CarInfo.ordinal,
                    ""
                ),
                EventManagerTypes.NavigationButton to EventManager(
                    EventMode.KeyEvent,
                    KeyCode.NAVIGATION.keycode,
                    "",
                    -1,
                    ""
                ),
                EventManagerTypes.ModeButton to EventManager(
                    EventMode.KeyEvent,
                    KeyCode.MODE_SWITCH.keycode,
                    "",
                    -1,
                    ""
                ),
                EventManagerTypes.MediaNext to EventManager(
                    EventMode.KeyEvent,
                    KeyCode.MEDIA_NEXT.keycode,
                    "",
                    -1,
                    ""
                ),
                EventManagerTypes.MediaPrevious to EventManager(
                    EventMode.KeyEvent,
                    KeyCode.MEDIA_PREVIOUS.keycode,
                    "",
                    -1,
                    ""
                ),
                EventManagerTypes.MediaPlayPause to EventManager(
                    EventMode.KeyEvent,
                    KeyCode.MEDIA_PLAY_PAUSE.keycode,
                    "",
                    -1,
                    ""
                ),
                EventManagerTypes.VoiceCommandButton to EventManager(
                    EventMode.KeyEvent,
                    KeyCode.VOICE_ASSIST.keycode,
                    "",
                    -1,
                    ""
                ),
                EventManagerTypes.TelephoneButton to EventManager(
                    EventMode.KeyEvent,
                    KeyCode.CALL.keycode,
                    "",
                    -1,
                    ""
                ),
                EventManagerTypes.VolumeDecrease to EventManager(
                    EventMode.KeyEvent,
                    KeyCode.VOLUME_DOWN.keycode,
                    "",
                    -1,
                    ""
                ),
                EventManagerTypes.VolumeIncrease to EventManager(
                    EventMode.KeyEvent,
                    KeyCode.VOLUME_UP.keycode,
                    "",
                    -1,
                    ""
                ),
                EventManagerTypes.TelephoneButtonLongPress to EventManager(
                    EventMode.NoAssignment,
                    -1,
                    "",
                    -1,
                    ""
                ),
                EventManagerTypes.TelephoneButtonPickUp to EventManager(
                    EventMode.KeyEvent,
                    KeyCode.CALL.keycode,
                    "",
                    -1,
                    ""
                ),
                EventManagerTypes.TelephoneButtonHangUp to EventManager(
                    EventMode.KeyEvent,
                    KeyCode.ENDCALL.keycode,
                    "",
                    -1,
                    ""
                ),
                EventManagerTypes.HiCarAppButton to EventManager(
                    EventMode.KeyEvent,
                    KeyCode.APP_SWITCH.keycode,
                    "",
                    -1,
                    ""
                ),
                EventManagerTypes.HiCarVoiceButton to EventManager(
                    EventMode.KeyEvent,
                    KeyCode.VOICE_ASSIST.keycode,
                    "",
                    -1,
                    ""
                )
            )
        }
    }
}
