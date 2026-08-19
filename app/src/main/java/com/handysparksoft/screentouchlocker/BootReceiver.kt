package com.handysparksoft.screentouchlocker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        // Only ACTION_BOOT_COMPLETED is exempt from the background foreground-service start
        // restrictions. Acting on any other action (USER_PRESENT fired on every unlock) throws
        // ForegroundServiceStartNotAllowedException on API 35+.
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return

        runCatching { ScreenTouchLockerService.startTheService(context) }
    }
}
