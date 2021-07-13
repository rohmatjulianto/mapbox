package com.joule.map.Utils

import android.app.Activity
import android.content.pm.PackageManager


class PermissionUtils {
    companion object {
        fun hasPermission(activity: Activity, permission: String?): Boolean {
            return activity.checkSelfPermission(permission!!) == PackageManager.PERMISSION_GRANTED
        }

        fun requestPermissions(activity: Activity, permission: Array<String>, requestId: Int) {
            activity.requestPermissions(permission, requestId)
        }

        fun isPermissionGranted(
            grantPermissions: Array<out String>, grantResults: IntArray,
            permission: String
        ): Boolean {
            for (i in grantPermissions.indices) {
                if (permission == grantPermissions[i]) {
                    return grantResults[i] == PackageManager.PERMISSION_GRANTED
                }
            }
            return false
        }
    }

}