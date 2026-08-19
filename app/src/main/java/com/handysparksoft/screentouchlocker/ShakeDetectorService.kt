package com.handysparksoft.screentouchlocker

import android.app.Service
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.hardware.SensorManager
import android.os.IBinder
import com.squareup.seismic.ShakeDetector

class ShakeDetectorService : Service() {

    private lateinit var shakeDetector: ShakeDetector
    private val onShakeListener = ShakeDetector.Listener {
        this@ShakeDetectorService.logdAndToast("Shake detected")
        ScreenTouchLockerService.startTheService(
            context = this@ShakeDetectorService,
            action = ScreenTouchLockerAction.ActionShake,
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // A null intent is redelivered when the system restarts this START_STICKY service after
        // the process was killed, so it must be handled instead of dereferenced.
        val action = intent?.action

        // Stop the service if we receive the Stop action.
        // START_NOT_STICKY is important here, we don't want the service to be relaunched.
        if (action == ShakeDetectorAction.ActionShakeStop.name) {
            stopSelf()
            logdAndToast("Shake detector stopped")
            return START_NOT_STICKY
        }
        logdAndToast("Shake detector started")
        startShakeDetectorService()
        return START_STICKY
    }

    private fun startShakeDetectorService() {
        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        shakeDetector = ShakeDetector(onShakeListener)
        shakeDetector.setSensitivity(ShakeDetector.SENSITIVITY_HARD + SENSITIVITY_INCREMENT)
        shakeDetector.start(sensorManager)
    }

    override fun onDestroy() {
        logdAndToast("Shake Detector stopped")
        if (::shakeDetector.isInitialized) {
            shakeDetector.stop()
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? = null

    companion object {

        fun startTheService(context: Context) {
            Intent(context, ShakeDetectorService::class.java).also {
                context.startService(it)
            }
        }

        fun stopShakeDetectorService(context: ContextWrapper) {
            Intent(context, ShakeDetectorService::class.java).also {
                it.action = ShakeDetectorAction.ActionShakeStop.name
                context.startService(it)
            }
        }
    }
}

enum class ShakeDetectorAction { ActionShakeStop }

private const val SENSITIVITY_INCREMENT = 10
