package com.example.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType

/**
 * Manages subtle haptic feedback for user interactions across the app,
 * such as button clicks, member deletions, and the primary 'Calculate' action.
 *
 * Utilizes Compose [HapticFeedback] and system [Vibrator] for crisp, tactile responsiveness.
 */
object HapticFeedbackManager {

    /**
     * Subtle, light haptic feedback for regular button clicks (e.g. Add Member, Print, Share, Dialog Cancel).
     */
    fun performButtonClick(context: Context? = null, haptic: HapticFeedback? = null) {
        try {
            haptic?.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            triggerVibration(context, durationMs = 12, amplitude = 55, predefinedEffect = VibrationEffect.EFFECT_TICK)
        } catch (_: Exception) {
            // Ignore if device lacks vibration hardware or permission is restricted
        }
    }

    /**
     * Satisfying, punchy haptic pulse for the primary 'Calculate' action.
     */
    fun performCalculateAction(context: Context? = null, haptic: HapticFeedback? = null) {
        try {
            haptic?.performHapticFeedback(HapticFeedbackType.LongPress)
            triggerVibration(context, durationMs = 38, amplitude = 140, predefinedEffect = VibrationEffect.EFFECT_CLICK)
        } catch (_: Exception) {
        }
    }

    /**
     * Crisp haptic feedback for destructive actions like deleting a member or clearing data.
     */
    fun performDeleteAction(context: Context? = null, haptic: HapticFeedback? = null) {
        try {
            haptic?.performHapticFeedback(HapticFeedbackType.LongPress)
            triggerVibration(context, durationMs = 28, amplitude = 110, predefinedEffect = VibrationEffect.EFFECT_HEAVY_CLICK)
        } catch (_: Exception) {
        }
    }

    private fun triggerVibration(
        context: Context?,
        durationMs: Long,
        amplitude: Int,
        predefinedEffect: Int
    ) {
        if (context == null) return
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    try {
                        vibrator.vibrate(VibrationEffect.createPredefined(predefinedEffect))
                    } catch (_: Exception) {
                        vibrator.vibrate(VibrationEffect.createOneShot(durationMs, amplitude.coerceIn(1, 255)))
                    }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(durationMs, amplitude.coerceIn(1, 255)))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(durationMs)
                }
            }
        } catch (_: Exception) {
        }
    }
}
