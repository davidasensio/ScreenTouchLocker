package com.handysparksoft.screentouchlocker.ui.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.handysparksoft.screentouchlocker.R
import com.handysparksoft.screentouchlocker.platform.Prefs
import com.handysparksoft.screentouchlocker.ui.common.Toggle
import kotlinx.coroutines.launch

@Composable
fun ConfigOptions(modifier: Modifier) {
    val context = LocalContext.current
    val prefs = Prefs(context)
    val scope = rememberCoroutineScope()

    val (showLockedTouches, onShowLockedTouchesValueChange) = remember { mutableStateOf(prefs.showLockedTouches) }
    val (vibrate, onVibrateValueChange) = remember { mutableStateOf(prefs.vibrate) }
    val (enableShakeAndLock, onEnableShakeAndLockValueChange) = remember { mutableStateOf(prefs.enableShakeAndLock) }
    val (enableShakeAndUnlock, onEnableShakeAndUnlockValueChange) = remember { mutableStateOf(prefs.enableShakeAndUnlock) }

    Surface {
        Column(modifier = modifier) {
            Text(
                text = stringResource(id = R.string.config_options),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp),
            )

            Column(modifier = Modifier.padding(start = 16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = stringResource(R.string.config_option_show_locked_touches))
                    Spacer(modifier = Modifier.weight(1f))
                    Toggle(
                        checked = showLockedTouches,
                        onCheckedChange = { checked ->
                            onShowLockedTouchesValueChange(checked)
                            scope.launch { prefs.showLockedTouches = checked }
                        },
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = stringResource(R.string.config_option_vibrate))
                    Spacer(modifier = Modifier.weight(1f))
                    Toggle(
                        checked = vibrate,
                        onCheckedChange = { checked ->
                            onVibrateValueChange(checked)
                            scope.launch { prefs.vibrate = checked }
                        },
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = stringResource(R.string.config_option_enable_shake_and_lock))
                    Spacer(modifier = Modifier.weight(1f))
                    Toggle(
                        checked = enableShakeAndLock,
                        onCheckedChange = { checked ->
                            onEnableShakeAndLockValueChange(checked)
                            scope.launch { prefs.enableShakeAndLock = checked }
                        },
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = stringResource(R.string.config_option_enable_shake_and_unlock))
                    Spacer(modifier = Modifier.weight(1f))
                    Toggle(
                        checked = enableShakeAndUnlock,
                        onCheckedChange = { checked ->
                            onEnableShakeAndUnlockValueChange(checked)
                            scope.launch { prefs.enableShakeAndUnlock = checked }
                        },
                    )
                }
            }
        }
    }
}
