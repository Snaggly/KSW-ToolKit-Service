package com.snaggly.ksw_toolkit.core.service.mcu

import com.snaggly.ksw_toolkit.core.service.mcu.parser.ScreenSwitchEvent
import com.snaggly.ksw_toolkit.core.service.view.BackTapper
import projekt.auto.mcu.ksw.serial.McuCommunicator
import projekt.auto.mcu.ksw.serial.collection.McuCommands
import projekt.auto.mcu.ksw.serial.reader.Reader
import projekt.auto.mcu.ksw.serial.writer.Writer

class CustomMcuCommunicator(private var backTapper: BackTapper, mcuWriter: Writer?, mcuReader: Reader?) : McuCommunicator(mcuWriter, mcuReader) {

    override fun sendCommand(cmdType: Int, data: ByteArray, update: Boolean) {
        if (cmdType == 0xE8 || cmdType == 0x00 || update || data.isEmpty()) { //Prevent MCUUpdates -> WILL BREAK MCU!!
            return
        }
        else if (cmdType == 0x6C) {
            McuLogic.setTurnedOffScreen(data.contentEquals(byteArrayOf(2,0)), backTapper)
        }
        else if (cmdType == 0x63) {
            if (data.size > 1) {
                if (data[0] == 1.toByte() && data[1] > 0)
                    McuLogic.setRealSysMode(1, backTapper)
            }
        }
        else if (cmdType == 0x67) {
            if (data[0] == 0.toByte() || data[0] == 5.toByte() || data[0] == 6.toByte() || data[0] == 8.toByte() || data[0] == 9.toByte() || data[0] == 10.toByte() || data[0] == 11.toByte() || data[0] == 12.toByte()) {
                McuLogic.setRealSysMode(2, backTapper)
            } else {
                McuLogic.setRealSysMode(1, backTapper)
            }
        }
        else if (cmdType == 0x68) {
            if (data[0] == 4.toByte())
                McuLogic.setRealSysMode(1, backTapper)
        }
        else if (cmdType == 0x69) {
            if (data.size > 1 && data[0] == 18.toByte()) {
                if (data[1] > 1)
                    data[1] = 2
                if (McuLogic.realSysMode == data[1].toInt())
                    return
            }
        }

        super.sendCommand(cmdType, data, false)
    }

    override fun sendCommand(mcuCommands: McuCommands) {
        Thread {
            this.sendCommand(mcuCommands.command, mcuCommands.data, mcuCommands.update)
        }.start()
    }

    fun sendForcedCommand(mcuCommands: McuCommands) {
        Thread {
            super.sendCommand(mcuCommands.command, mcuCommands.data, false)
        }.start()
    }
}