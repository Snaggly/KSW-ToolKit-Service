package com.snaggly.ksw_toolkit.util.list.eventtype

enum class EventMode(val value: Int) {
    NoAssignment(0),
    KeyEvent(1),
    StartApp(2),
    McuCommand(3);

    companion object
}