package com.handysparksoft.screentouchlocker

import android.Manifest
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat

/**
 * Permissions functions
 */
fun Context.getOverlayPermissionIntent() = Intent(
    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
    Uri.parse("package:$packageName"),
).apply {
    flags = Intent.FLAG_ACTIVITY_NEW_TASK
}

fun Context.drawOverOtherAppsEnabled(): Boolean = Settings.canDrawOverlays(this)

fun Context.requestOverlayPermission() {
    startActivity(getOverlayPermissionIntent())
}

fun Context.postNotificationsEnabled(): Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.POST_NOTIFICATIONS,
    ) == PackageManager.PERMISSION_GRANTED
} else {
    true // Permission not required on lower versions (Only 33+)
}

/**
 * Log functions
 */
fun ContextWrapper.logd(message: String) {
    if (BuildConfig.DEBUG) {
        Log.d(this::class.simpleName, "*** $message")
    }
}

fun ContextWrapper.toast(message: String) {
    if (BuildConfig.DEBUG) {
        Log.d(this::class.simpleName, message)
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

fun ContextWrapper.logdAndToast(message: String) {
    if (BuildConfig.DEBUG) {
        this.logd(message)
        this.toast(message)
    }
}

/**
 * Vibrate
 */
@Suppress("DEPRECATION")
fun Context.vibrate() {
    (this.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator)?.let { vibrator ->
        vibrator.vibrate(VibrationEffect.createOneShot(250, VibrationEffect.DEFAULT_AMPLITUDE))
    }
}
