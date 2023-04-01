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

        super.sendCommand(cmdType, data, false)
    }

    override fun sendCommand(mcuCommands: McuCommands) {
        Thread {
            this.sendCommand(mcuCommands.command, mcuCommands.data, mcuCommands.update)
        }.start()
    }
}