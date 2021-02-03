package com.snaggly.ksw_toolkit.util.enums

enum class EventManagerTypes {
    VoiceCommandButton,
    TelephoneButton,
    TelephoneButtonHangUp,
    TelephoneButtonLongPress,
    MediaPrevious,
    MediaNext,
    MediaPlayPause,
    ModeButton,
    KnobPress,
    KnobTiltUp,
    KnobTiltDown,
    KnobTiltLeft,
    KnobTiltRight,
    KnobTurnLeft,
    KnobTurnRight,
    MenuButton,
    BackButton,
    OptionsButton,
    NavigationButton,
    Idle,
    CarData,
    BenzData,
    Dummy;

    companion object {
        private val types = EventManagerTypes.values().associateBy { it.name }
        fun findByName(value: String) = types[value]
    }
}