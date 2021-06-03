package com.github.code.gambit.utility

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.io.File
import javax.inject.Named

class SystemManager
constructor(private val ctx: Context, @Named(AppConstant.Named.PERMISSION_ARRAY)val permissions: List<String>) {

    fun checkPermission(fragment: Fragment, permissionResult: (success: Boolean) -> Unit) {
        if (!checkAllPermissions()) {
            requestPermission(fragment, permissionResult)
        }
    }

    /** Permission Checking  */
    private fun checkAllPermissions(): Boolean {
        var hasPermissions = true
        for (permission in permissions) {
            hasPermissions = hasPermissions and isPermissionGranted(permission)
        }
        return hasPermissions
    }

    private fun isPermissionGranted(permission: String) = ContextCompat.checkSelfPermission(ctx, permission) == PackageManager.PERMISSION_GRANTED

    private fun requestPermission(fragment: Fragment, permissionResult: (success: Boolean) -> Unit) {
        val launcher = fragment.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            var all = true
            it.forEach { perm ->
                if (!perm.value) {
                    all = false
                }
            }
            if (all) {
                permissionResult(true)
            } else {
                permissionResult(false)
            }
        }
        launcher.launch(permissions.toTypedArray())
    }

    fun requestFile(activity: ComponentActivity, result: (file: Uri?) -> Unit): ActivityResultLauncher<Intent> {
        return activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val st = it.data!!.data
                if (st == null) {
                    result(null)
                    return@registerForActivityResult
                }
                result(st)
            }
        }
    }

    fun requestImage(fragment: Fragment, result: (imageUri: Uri?) -> Unit): ActivityResultLauncher<Intent> {
        return fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                result(it.data!!.data)
            } else {
                result(null)
            }
        }
    }

    fun launchActivity(launcher: ActivityResultLauncher<Intent>, intent: Intent) {
        launcher.launch(intent)
    }

    fun getFileName(uri: Uri): String {
        val cursor = ctx.contentResolver.query(uri, null, null, null, null)
        val nameIndex = cursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        val name = cursor.getString(nameIndex)
        cursor.close()
        return name
    }

    fun getFileSize(uri: Uri): Int {
        val cursor = ctx.contentResolver.query(uri, null, null, null, null)
        val sizeIndex = cursor!!.getColumnIndex(OpenableColumns.SIZE)
        cursor.moveToFirst()
        val size = cursor.getInt(sizeIndex)
        cursor.close()
        return size
    }

    fun getFilePath(uri: Uri): String {
        val path = FileUtil.getPathFromLocalUri(ctx, uri)
        if (path != null) {
            return path
        }
        val ins = ctx.contentResolver.openInputStream(uri)
        val file = File(ctx.externalCacheDir!!.absolutePath + "/test")
        file.writeBytes(ins!!.readBytes())
        return file.absolutePath
    }

    fun isOnline(): Boolean {
        val connectivityManager =
            ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    return true
                }
            }
        }
        return false
    }
}
