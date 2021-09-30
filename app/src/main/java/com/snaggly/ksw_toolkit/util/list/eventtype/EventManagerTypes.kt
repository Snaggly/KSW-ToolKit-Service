package com.snaggly.ksw_toolkit.util.list.eventtype

enum class EventManagerTypes {
    VoiceCommandButton,
    TelephoneButton,
    TelephoneButtonHangUp,
    TelephoneButtonLongPress,
    VolumeDecrease,
    VolumeIncrease,
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
    ScreenSwitch,
    Dummy;

    companion object
}