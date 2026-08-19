package com.handysparksoft.screentouchlocker

import android.app.PendingIntent
import android.content.ComponentName
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class ScreenTouchLockerTileService : TileService() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action?.let { action ->
            when (action) {
                TileAction.ActionTileStart.name -> updateTileState(true)
                TileAction.ActionTileStop.name -> updateTileState(false)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onTileAdded() {
        super.onTileAdded()
        updateTileState(false)
    }

    override fun onClick() {
        super.onClick()
        val activeState = qsTile.state == Tile.STATE_ACTIVE
        if (activeState) {
            // Turn off
            updateTileState(false)
            ScreenTouchLockerService.startTheService(context = this, action = ScreenTouchLockerAction.ActionUnlock)
        } else {
            // Turn on
            if (drawOverOtherAppsEnabled()) {
                updateTileState(true)
                ScreenTouchLockerService.startTheService(context = this, action = ScreenTouchLockerAction.ActionLock)
                ShakeDetectorService.startTheService(context = this)
            } else {
                updateTileState(false)
                collapseAndRequestOverlayPermission()
            }
        }
    }

    /** startActivityAndCollapse(Intent) throws UnsupportedOperationException from API 34 on, so
     *  the PendingIntent overload has to be used there. It only exists from API 34, and minSdk is 29.
     *  The legacy branch is unreachable on API 34+, but lint flags the call regardless of the
     *  version guard, so the check is suppressed rather than the minSdk 29 path dropped.
     */
    @Suppress("StartActivityAndCollapseDeprecated")
    private fun collapseAndRequestOverlayPermission() {
        val overlayIntent = getOverlayPermissionIntent()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startActivityAndCollapse(
                PendingIntent.getActivity(this, 0, overlayIntent, PendingIntent.FLAG_IMMUTABLE)
            )
        } else {
            @Suppress("DEPRECATION")
            startActivityAndCollapse(overlayIntent)
        }
    }

    private fun updateTileState(activeState: Boolean) {
        if (qsTile != null) {
            qsTile?.state = if (activeState) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            qsTile?.updateTile()
        } else {
            requestListeningState(this, ComponentName(this, ScreenTouchLockerTileService::class.java))
        }
    }

    companion object {

        fun startTileService(context: ContextWrapper) {
            Intent(context, ScreenTouchLockerTileService::class.java).also {
                it.action = TileAction.ActionTileStart.name
                context.startService(it)
                context.logdAndToast("Tile started")
            }
        }

        fun stopTileService(context: ContextWrapper) {
            Intent(context, ScreenTouchLockerTileService::class.java).also {
                it.action = TileAction.ActionTileStop.name
                context.startService(it)
                context.logdAndToast("Tile stopped")
            }
        }
    }
}

enum class TileAction { ActionTileStart, ActionTileStop }
