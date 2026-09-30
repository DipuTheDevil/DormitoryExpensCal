package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.ui.theme.HindSiliguriFontFamily
import com.example.ui.theme.TextMuted

/**
 * Custom-styled AlertDialog for 'সব ক্লিয়ার করুন' functionality
 * implementing the app's Dark Navy Glassmorphism design language.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    BasicAlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        val glassCardGradient = Brush.verticalGradient(
            colors = listOf(
                Color(0xF20F172A), // 95% deep navy
                Color(0xF21E293B)  // 95% light navy
            )
        )
        val glassBorderColor = Color(0x33FFFFFF) // 20% white border

        Box(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .widthIn(max = 420.dp)
                .fillMaxWidth()
                .shadow(
                    elevation = 24.dp,
                    shape = RoundedCornerShape(20.dp),
                    ambientColor = Color(0x99000000),
                    spotColor = Color(0x99000000)
                )
                .clip(RoundedCornerShape(20.dp))
                .background(glassCardGradient)
                .border(1.dp, glassBorderColor, RoundedCornerShape(20.dp))
                .padding(24.dp)
                .testTag("reset_confirm_dialog")
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. Alert Icon Badge with subtle red frosted background
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color(0x26EF4444)) // 15% red
                        .border(1.dp, Color(0x4DEF4444), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🗑️",
                        fontSize = 24.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 2. Dialog Title
                Text(
                    text = stringResource(R.string.dialog_reset_title),
                    color = Color.White,
                    fontFamily = HindSiliguriFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 3. Message
                Text(
                    text = stringResource(R.string.dialog_reset_message),
                    color = Color(0xFFE2E8F0),
                    fontFamily = HindSiliguriFontFamily,
                    fontSize = 14.5.sp,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "সদস্যদের নাম ও খরচের সমস্ত হিসাব মুছে ডিফল্ট অবস্থায় ফিরে যাবে।",
                    color = TextMuted,
                    fontFamily = HindSiliguriFontFamily,
                    fontSize = 12.5.sp,
                    lineHeight = 18.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 4. Action Buttons (Cancel / Confirm)
                val context = LocalContext.current
                val haptic = LocalHapticFeedback.current

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Cancel Button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x1AFFFFFF)) // rgba(255, 255, 255, 0.10)
                            .border(1.dp, Color(0x26FFFFFF), RoundedCornerShape(10.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(color = Color.White)
                            ) {
                                com.example.utils.HapticFeedbackManager.performButtonClick(context, haptic)
                                onDismiss()
                            }
                            .testTag("cancel_reset_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.dialog_cancel),
                            color = Color(0xFFE2E8F0),
                            fontFamily = HindSiliguriFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }

                    // Confirm Reset Button (Danger Red Accent)
                    val confirmGradient = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFDC2626),
                            Color(0xFFB91C1C)
                        )
                    )

                    Box(
                        modifier = Modifier
                            .weight(1.15f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(confirmGradient)
                            .border(1.dp, Color(0x66F87171), RoundedCornerShape(10.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(color = Color.White)
                            ) {
                                com.example.utils.HapticFeedbackManager.performDeleteAction(context, haptic)
                                onConfirm()
                            }
                            .testTag("confirm_reset_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.dialog_confirm),
                            color = Color.White,
                            fontFamily = HindSiliguriFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
