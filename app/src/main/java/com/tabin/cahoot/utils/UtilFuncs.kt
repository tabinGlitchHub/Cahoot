package com.tabin.cahoot.utils

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Base64
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.tabin.cahoot.models.UserModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.Collections
import java.util.Locale


class UtilFuncs {

    companion object {
        val TAG: String = "UtilsFuncs : "

        /**
         * Get IP address from first non-localhost interface
         * @param useIPv4   true=return ipv4, false=return ipv6
         * @return  address or empty string
         */
        fun getIPAddress(useIPv4: Boolean): String? {
            try {
                val interfaces: List<NetworkInterface> =
                    Collections.list(NetworkInterface.getNetworkInterfaces())
                for (intf in interfaces) {
                    val addrs: List<InetAddress> = Collections.list(intf.inetAddresses)
                    for (addr in addrs) {
                        Log.d(TAG, "getIPAddress: address = " + addr.hostAddress)
                        if (!addr.isLoopbackAddress) {
                            val sAddr = addr.hostAddress
                            //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                            val isIPv4 = sAddr.indexOf(':') < 0
                            if (useIPv4) {
                                if (isIPv4) return sAddr
                            } else {
                                if (!isIPv4) {
                                    val delim = sAddr.indexOf('%') // drop ip6 zone suffix
                                    return if (delim < 0) sAddr.uppercase(Locale.getDefault()) else sAddr.substring(
                                        0, delim
                                    ).uppercase(
                                        Locale.getDefault()
                                    )
                                }
                            }
                        }
                    }
                }
            } catch (ignored: Exception) {
            } // for now eat exceptions
            return ""
        }


        fun getEncryptedValueOf(string: String): String {
            return Base64.encodeToString(string.toByteArray(Charsets.UTF_8), Base64.NO_PADDING)
        }

        fun getDecryptedValueOf(string: String): String {
            return Base64.decode(string, Base64.NO_PADDING).toString(Charsets.UTF_8);
        }

        fun getSharedPrefsForKey(key: String, context: Context): String {
            val pref =
                context.getSharedPreferences(Constants.SHARED_PREF_FILE_NAME, Context.MODE_PRIVATE)
            return pref.getString(key, "") ?: ""
        }

        fun setSharedPrefsForKey(key: String, value: String, context: Context) {
            val pref =
                context.getSharedPreferences(Constants.SHARED_PREF_FILE_NAME, Context.MODE_PRIVATE)
            val editor = pref.edit()
            editor.putString(key, value)
            editor.apply()
        }

        fun areAllUserReady(connectedMembersList: SnapshotStateList<UserModel>): Boolean {
            for (user: UserModel in connectedMembersList) {
                if (!user.isReady) return false
            }
            return true
        }

        // to get the type of file
        private val mimeTypeMap = MimeTypeMap.getSingleton()

        @SuppressLint("Range")
        fun getFilePathFromUri(uri: Uri?, context: Context): String? {
            return ""
        }

        /**
         * Tries to get actual name of the file being copied.
         * This might be required in some of the cases where you might want to know the file name too.
         *
         * @param uri
         *
         */
        @SuppressLint("Recycle")
        fun getFileNameFromUri(uri: Uri, context: Context): String? {
            println(uri.encodedPath)
            val returnCursor: Cursor = context.contentResolver.query(uri, null, null, null, null) ?: return null
            val nameIndex: Int = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            returnCursor.moveToFirst()
            val name: String = returnCursor.getString(nameIndex)
            returnCursor.close()
            return name
        }

        private fun getFileExtension(uri: Uri, context: Context): String? {
            return mimeTypeMap.getExtensionFromMimeType(context.contentResolver.getType(uri))
        }

        private val uiScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

        fun runOnUiThread(block: suspend () -> Unit) = uiScope.launch { block() }
    }
}