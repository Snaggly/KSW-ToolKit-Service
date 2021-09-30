package com.snaggly.ksw_toolkit.core.service.remote

import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.os.Binder
import projekt.auto.mcu.encryption.Base64
import java.security.KeyFactory
import java.security.PublicKey
import java.security.Signature
import java.security.spec.X509EncodedKeySpec

object ServiceValidation {
    var hasAuthenticated = false
    var signature : ByteArray? = null

    fun validate(context: Context) : Boolean {
        hasAuthenticated = false

        val packageName = context.packageManager.getNameForUid(Binder.getCallingUid()) ?: return false
        if (packageName in allowedPackages)
            return true
        else if (signature==null)
            return false
        val s = Signature.getInstance("SHA256withRSA/PSS").apply {
            initVerify(getPublicKey())
            update(packageName.toByteArray())
        }

        if (s.verify(signature)) {
            hasAuthenticated = true
            return true
        }

        return false
    }



    private fun getPackageNames(context: Context, pid: Int): Array<String?>? {
        val am = context.getSystemService(Service.ACTIVITY_SERVICE) as ActivityManager
        val infos = am.runningAppProcesses
        if (infos != null && infos.size > 0) {
            for (info in infos) {
                if (info.pid == pid) {
                    return info.pkgList
                }
            }
        }
        return null
    }

    private fun getPublicKey() : PublicKey {
        return KeyFactory.getInstance("RSA")
            .generatePublic(
                X509EncodedKeySpec(
                Base64.decode(publicKeyBase64, Base64.DEFAULT)
            )
            )
    }

    private val allowedPackages : Array<String> = arrayOf(
        "com.snaggly.wits.ksw_toolkit.client"
    )

    private const val publicKeyBase64 =
        "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3EJuFxL6kgtGpXRmt20e" +
        "qUfiBQTZiMZyWMhzhwgtHpkcX6RXKiVaqLy0iTvRL7QxfyJcgLAaxYh7e1iS3SHm" +
        "4cbMCpmF3CvcfAmaCmVmoocUE3GwXzdSifUcia8XNXiEN8V0polYKMGV6lvbqVFg" +
        "qpZtZOsznidlJ6clbDJlSV8EJBoRFw0LsFSvN9BB4LRL0Q+uFEQek5qkJt2VpoBu" +
        "syHc09+BIr0X/rFpxZPdkBnI5Uy8+0g0aJmEFPwhlP2jSiC4yHq/zzXusiDlqT1/" +
        "iRQN1XXaJAhgHdFD3HA37fVIAuSUKshcW/0T9xrxp+PDCXZPj1aCFekKnXzaqsgM" +
        "RwIDAQAB"
}