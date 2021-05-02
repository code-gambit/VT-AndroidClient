package com.github.code.gambit.utility

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import javax.inject.Named

class SystemManager
constructor(val ctx: Context, @Named(AppConstant.Named.PERMISSION_ARRAY)val permissions: List<String>) {

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

    fun requestImage(fragment: Fragment, result: (imageUri: Uri?) -> Unit): ActivityResultLauncher<Intent> {
        return fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                result(it.data!!.data)
            } else {
                result(null)
            }
        }
    }

    fun launchActivity(launcher: ActivityResultLauncher<Intent>) {
        launcher.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
    }
}
