package com.handysparksoft.screentouchlocker.classic

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import com.handysparksoft.screentouchlocker.R
import com.handysparksoft.screentouchlocker.platform.Prefs
import com.handysparksoft.screentouchlocker.vibrate

class LockerWindow(val context: Context, private val onCloseWindow: () -> Unit) {
    private val prefs by lazy { Prefs(context) }

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val rootView = layoutInflater.inflate(R.layout.locker_window_layout, null)
    private val contentView by lazy { rootView.findViewById<LockerWindowContent>(R.id.lockerWindowContent) }

    private val windowFlags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
    private val windowParams = WindowManager.LayoutParams(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.MATCH_PARENT, // Display it on top of other application windows
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, // Don't let it grab the input focus
        windowFlags, // Window Flags
        PixelFormat.TRANSLUCENT, // Make the underlying application window visible
    )

    init {
        initWindowParams()
        initWindow()
    }

    private fun initWindowParams() {
        windowParams.gravity = Gravity.CENTER
    }

    private fun initWindow() {
        val background = rootView.findViewById<View>(R.id.lockerWindowBackground)

        background.animate().setDuration(5000).alpha(0f).start()
        contentView.onCloseListener = {
            close()
        }
    }

    fun open() {
        try {
            windowManager.addView(rootView, windowParams)
            rootView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            if (prefs.vibrate) {
                context.vibrate()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            rootView.findViewById<LockerWindowContent>(R.id.lockerWindowContent).apply {
                resetState()
                animateUnlockButton()
            }
        }
    }

    fun close() {
        try {
            windowManager.removeView(rootView)
            onCloseWindow()
            if (prefs.vibrate) {
                context.vibrate()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
