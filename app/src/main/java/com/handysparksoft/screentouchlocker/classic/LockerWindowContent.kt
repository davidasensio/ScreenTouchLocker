package com.handysparksoft.screentouchlocker.classic

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.handysparksoft.screentouchlocker.R
import com.handysparksoft.screentouchlocker.logdAndToast
import com.handysparksoft.screentouchlocker.platform.Prefs
import java.util.Timer
import java.util.TimerTask

class LockerWindowContent @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    FrameLayout(context, attrs) {

    private val prefs by lazy { Prefs(context) }

    private inner class CloseTimerTask : TimerTask() {

        override fun run() {
            onCloseListener?.invoke()
        }
    }

    private var closeTimerTask: TimerTask? = null
    var onCloseListener: (() -> Unit)? = null

    init {
        View.inflate(context, R.layout.locker_window_content_layout, this)
        bindUI()
    }

    fun resetState() {
        resetProgressAnimation()
    }

    private fun bindUI() {
        setUnlockButton()
        setUnlockProgressFeedback()
        setLockedTouchFeedback()
    }

    private fun setUnlockButton() {
        val unlockButton = rootView.findViewById<ImageView>(R.id.unlockButton)
        animateUnlockButton()
        unlockButton.setOnClickListener {
            unlockButton.alpha = 1f
            fadeOutUnlockButton(unlockButton)
        }
    }

    fun animateUnlockButton() {
        val unlockButton = rootView.findViewById<ImageView>(R.id.unlockButton)
        (unlockButton.drawable as? AnimatedVectorDrawable)?.start()
        fadeOutUnlockButton(unlockButton)
    }

    private fun fadeOutUnlockButton(unlockButton: ImageView) {
        unlockButton.alpha = 1f
        unlockButton.animate()
            .alpha(UNLOCK_BUTTON_ALPHA_VALUE)
            .setStartDelay(UNLOCK_BUTTON_FADE_DELAY)
            .setDuration(UNLOCK_BUTTON_FADE_DURATION)
            .start()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUnlockProgressFeedback() {
        val unlockButtonProgress = rootView.findViewById<ImageView>(R.id.unlockButtonProgress)
        val unlockButton = rootView.findViewById<ImageView>(R.id.unlockButton)

        unlockButton.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    unlockButtonProgress.visibility = VISIBLE
                    (unlockButtonProgress.drawable as? AnimatedVectorDrawable)?.start()
                    startCloseTimerTask()
                }

                MotionEvent.ACTION_UP -> {
                    cancelCloseTimerTask()
                    resetProgressAnimation()
                }
            }
            unlockButton.performClick()
            true
        }
    }

    private fun setLockedTouchFeedback() {
        val screenContent = rootView.findViewById<View>(R.id.lockScreenContent)
        val touchLockedImage = rootView.findViewById<ImageView>(R.id.touchLockedImage)
        screenContent.setOnTouchListener { _, motionEvent ->
            locateTouch(touchLockedImage, motionEvent)

            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    performHapticFeedback(screenContent)
                    animateLockedTouch(touchLockedImage, motionEvent)
                    (context as ContextWrapper).logdAndToast("Touch!")
                }

                MotionEvent.ACTION_UP -> {
                    hideTouch(touchLockedImage)
                }
            }
            screenContent.performClick()
            true
        }
    }

    private fun resetProgressAnimation() {
        val unlockButtonProgress = rootView.findViewById<ImageView>(R.id.unlockButtonProgress)
        (unlockButtonProgress.drawable as? AnimatedVectorDrawable)?.reset()
    }

    private fun startCloseTimerTask() {
        closeTimerTask = CloseTimerTask()
        Timer().schedule(closeTimerTask, UNLOCK_PROGRESS_TIMER_DURATION)
    }

    private fun cancelCloseTimerTask() {
        closeTimerTask?.cancel()
        closeTimerTask = null
    }

    private fun performHapticFeedback(screenContent: View) {
        if (prefs.vibrate) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                screenContent.performHapticFeedback(HapticFeedbackConstants.REJECT)
            } else {
                screenContent.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            }
        }
    }

    private fun locateTouch(touchLockedImage: View, motionEvent: MotionEvent) {
        touchLockedImage.x = motionEvent.x - (touchLockedImage.width / 2)
        touchLockedImage.y = motionEvent.y - (touchLockedImage.height / 2)
    }

    private fun animateLockedTouch(touchLockedImage: ImageView, motionEvent: MotionEvent) {
        if (prefs.showLockedTouches) {
            touchLockedImage.visibility = View.VISIBLE
            touchLockedImage.animate().scaleX(3f).scaleY(3f).setDuration(LOCKED_TOUCH_SCALE_IN_DURATION).start()
            (touchLockedImage.drawable as? AnimatedVectorDrawable)?.start()
        }
    }

    private fun hideTouch(touchLockedImage: View) {
        touchLockedImage.animate().scaleX(1f).scaleY(1f).setDuration(LOCKED_TOUCH_SCALE_OUT_DURATION).withEndAction {
            touchLockedImage.visibility = View.INVISIBLE
        }.start()
    }

    companion object {

        private const val UNLOCK_PROGRESS_TIMER_DURATION = 2750L

        private const val UNLOCK_BUTTON_FADE_DELAY = 6000L
        private const val UNLOCK_BUTTON_FADE_DURATION = 2000L
        private const val UNLOCK_BUTTON_ALPHA_VALUE = 0.3f
        private const val LOCKED_TOUCH_SCALE_IN_DURATION = 300L
        private const val LOCKED_TOUCH_SCALE_OUT_DURATION = 50L
    }
}
