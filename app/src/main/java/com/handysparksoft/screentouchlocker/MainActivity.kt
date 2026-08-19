package com.handysparksoft.screentouchlocker

import android.Manifest
import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.google.android.play.core.review.ReviewManagerFactory
import com.handysparksoft.screentouchlocker.platform.InAppReviewManager
import com.handysparksoft.screentouchlocker.ui.onboarding.OnboardingScreen
import com.handysparksoft.screentouchlocker.ui.theme.ScreenTouchLockerTheme
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (intent.action == ScreenTouchLockerAction.ActionUnlock.toString()) {
            ScreenTouchLockerService.startTheService(context = this, action = ScreenTouchLockerAction.ActionUnlock)
        }

        startInAppReviewFlow(this)

        setContent {
            var canDrawOverlays by remember { mutableStateOf(drawOverOtherAppsEnabled()) }
            var canPostNotifications by remember { mutableStateOf(postNotificationsEnabled()) }
            val permissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission(),
            ) { isGranted: Boolean ->
                canPostNotifications = isGranted
            }

            ScreenTouchLockerTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent) {
                    OnboardingScreen(
                        canDrawOverlays = canDrawOverlays,
                        canPostNotifications = canPostNotifications,
                        onAskForPostNotificationsPermission = {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        },
                        onAskForOverlayPermission = {
                            canDrawOverlays = drawOverOtherAppsEnabled()
                            if (!canDrawOverlays) {
                                requestOverlayPermission()
                            }
                        },
                    )
                }
            }
        }
    }
}

private fun startInAppReviewFlow(context: Context) {
    // InAppReview request
    val askForAReview = Random.nextInt(3) == 1
    if (askForAReview) {
        (context as? Activity)?.let { activity ->
            InAppReviewManager(ReviewManagerFactory.create(context)).requestReviewFlow(activity)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ScreenTouchLockerTheme {
        OnboardingScreen(canDrawOverlays = false, canPostNotifications = false)
    }
}
