package com.snaggly.ksw_toolkit.util.adb

import android.content.Context
import android.net.TrafficStats
import projekt.auto.mcu.adb.AdbManager
import projekt.auto.mcu.adb.lib.AdbConnection
import projekt.auto.mcu.adb.lib.AdbCrypto
import projekt.auto.mcu.adb.lib.AdbStream
import java.io.File
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.security.NoSuchAlgorithmException

class AdbManager {
    private val socket = Socket()
    private var isConnected = false
    private lateinit var adbConnection: AdbConnection
    private lateinit var shellStream: AdbStream
    private var previousShellText = ""

    interface OnAdbShellDataReceived {
        fun onDataReceived(text: String)
    }

    @Throws(AdbConnectionException::class)
    fun connect(context: Context, destination: String, callback: OnAdbShellDataReceived): AdbStream {
        if (isConnected)
            disconnect()
        var outerException: Exception? = null
        val setup = Thread {
            try {
                TrafficStats.setThreadStatsTag(Thread.currentThread().id.toInt())
                val adbCrypto = setupCrypto(context.filesDir)
                socket.connect(InetSocketAddress("localhost", 5555), 5000)
                adbConnection = AdbConnection.create(socket, adbCrypto)
                adbConnection.connect()
                shellStream = adbConnection.open(destination)
            } catch (e: Exception) {
                outerException = e
            }
        }
        setup.start()
        setup.join()

        if (outerException != null) {
            throw AdbConnectionException(outerException!!)
        }

        Thread {
            while (!shellStream.isClosed) {
                var shellText = String(shellStream.read(), Charsets.US_ASCII)
                if (shellText != previousShellText) {
                    previousShellText = shellText
                    callback.onDataReceived(shellText)
                }
            }
        }.start()
        isConnected = true
        return shellStream
    }

    fun disconnect() {
        if (isConnected) {
            shellStream.close()
            adbConnection.close()
            socket.close()
        }
        isConnected = false
    }

    @Throws(NoSuchAlgorithmException::class, IOException::class)
    private fun setupCrypto(fileDir: File): AdbCrypto? {
        val publicKey = File(fileDir, "public.key")
        val privateKey = File(fileDir, "private.key")
        var c: AdbCrypto? = null
        if (publicKey.exists() && privateKey.exists()) {
            try {
                c = AdbCrypto.loadAdbKeyPair(AdbManager.getBase64Impl(), privateKey, publicKey)
            } catch (ignored: Exception) {
            }
        }
        if (c == null) {
            c = AdbCrypto.generateAdbKeyPair(AdbManager.getBase64Impl())
            c.saveAdbKeyPair(privateKey, publicKey)
        }
        return c
    }

    class AdbConnectionException(private val outerException: Exception) : Exception() {
        override fun getLocalizedMessage(): String? {
            return outerException.localizedMessage
        }

        override fun getStackTrace(): Array<StackTraceElement> {
            return outerException.stackTrace
        }
    }
}