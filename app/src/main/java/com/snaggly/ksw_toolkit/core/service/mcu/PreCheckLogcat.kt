package com.snaggly.ksw_toolkit.core.service.mcu

import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import kotlin.experimental.and


class PreCheckLogcat {
    // Obtain only the latest value of the specified data
    // Run before logcat -c is done in startReading()
    fun findMcuListner(findCommand: Int, find1stData: Byte): ByteArray {
        try {
            var logProc = Runtime.getRuntime().exec("logcat -d KswMcuListener:I *:S")
            val bufRead = BufferedReader(InputStreamReader(logProc.inputStream))
            var lastLine = ""
            var readLine: String? = ""
            while (bufRead.readLine().also { readLine = it } != null) {
                var line = readLine!!
                if (line.contains("--Mcu toString-----")) {
                    try {
                        line = line.substring(line.lastIndexOf('[') + 2, line.lastIndexOf(']') - 1)
                        line = line.replace("\\s+".toRegex(), "")
                        val splitString = line.split("-", limit = 2).toTypedArray()
                        val commandStr = splitString[0].substring(splitString[0].indexOf(":") + 1)
                        val command = commandStr.toInt(16)
                        val byteStrs = splitString[1].substring(splitString[1].indexOf(":") + 1)
                        val dataStrs = byteStrs.split("-").toTypedArray()
                        val data0 = dataStrs[0].toInt(16).toByte()
                        if (command == findCommand && data0 == find1stData) {
                            lastLine = line
                        }
                    }
                    catch (e : Exception) {
                        Log.e("PreCheckLogcat", "Received incorrect data \"$line\"", e)
                    }
                }
            }

            if (lastLine != "") {
                val splitString = lastLine.split("-", limit = 2).toTypedArray()
                if (splitString.size < 2) {
                    Log.e("PreCheckLogcat", "Received incorrect data \"$lastLine\"")
                }
                val byteStrs = splitString[1].substring(splitString[1].indexOf(":") + 1)
                val dataStrs = byteStrs.split("-").toTypedArray()
                val data = ByteArray(dataStrs.size)
                for (i in data.indices) {
                    try {
                        data[i] = dataStrs[i].toInt(16).toByte()
                    } catch (e: Exception) {
                        // We will just skip the parsing of any other objects here
                        Log.e("PreCheckLogcat", "Received incorrect data \"" + dataStrs[i] + "\"", e)
                    }
                }
                return data
            } else
                return ByteArray(0)

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ByteArray(0)
    }

    fun getLightsStatus() {
        var data = findMcuListner(0xA1, 0x10)
        if (data.size >= 3) {
            if (data[1].and(7) >= 1) {
                McuLogic.isAnyLightOn = true
            }
        }
    }
}