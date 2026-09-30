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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.ui.theme.HindSiliguriFontFamily
import com.example.ui.theme.TextMuted
import com.example.utils.HapticFeedbackManager
import com.example.utils.ShareReportHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareSummaryDialog(
    summaryText: String,
    onDismiss: () -> Unit,
    onExportCsv: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

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
                Color(0xF21E293B)  // 95% slate navy
            )
        )
        val glassBorderColor = Color(0x33FFFFFF)

        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 24.dp)
                .widthIn(max = 440.dp)
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
                .padding(20.dp)
                .testTag("share_summary_dialog")
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. Header Icon
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color(0x2610B981))
                        .border(1.dp, Color(0x6610B981), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📲",
                        fontSize = 24.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 2. Title
                Text(
                    text = "হিসাব বিবরণী শেয়ার করুন",
                    color = Color.White,
                    fontFamily = HindSiliguriFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "নিচের টেক্সট মেসেজটি সরাসরি WhatsApp বা অন্যান্য অ্যাপে পাঠাতে পারেন:",
                    color = TextMuted,
                    fontFamily = HindSiliguriFontFamily,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 3. Scrollable Preview Box
                val scrollState = rememberScrollState()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x660B1120))
                        .border(1.dp, Color(0x1FFFFFFF), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                        .verticalScroll(scrollState)
                ) {
                    Text(
                        text = summaryText,
                        color = Color(0xFFE2E8F0),
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 12.5.sp,
                        lineHeight = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 4. Primary WhatsApp Button
                val whatsappGradient = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF10B981),
                        Color(0xFF059669)
                    )
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(whatsappGradient)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(color = Color.White)
                        ) {
                            HapticFeedbackManager.performButtonClick(context, haptic)
                            ShareReportHelper.shareDirectWhatsApp(context, summaryText)
                            onDismiss()
                        }
                        .testTag("dialog_share_whatsapp_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "💬 WhatsApp-এ সরাসরি পাঠান",
                        color = Color.White,
                        fontFamily = HindSiliguriFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 5. Secondary Action Buttons: Copy Text & Other Apps
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Copy to Clipboard
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x26FFFFFF))
                            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(10.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(color = Color.White)
                            ) {
                                HapticFeedbackManager.performButtonClick(context, haptic)
                                ShareReportHelper.copyToClipboard(context, summaryText)
                            }
                            .testTag("dialog_copy_text_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "📋 কপি করুন",
                            color = Color.White,
                            fontFamily = HindSiliguriFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.5.sp
                        )
                    }

                    // Share to other apps (system chooser)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF334155))
                            .border(1.dp, Color(0x4D64748B), RoundedCornerShape(10.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(color = Color.White)
                            ) {
                                HapticFeedbackManager.performButtonClick(context, haptic)
                                ShareReportHelper.shareText(context, summaryText)
                                onDismiss()
                            }
                            .testTag("dialog_share_other_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "📤 অন্যান্য অ্যাপ",
                            color = Color(0xFFF8FAFC),
                            fontFamily = HindSiliguriFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.5.sp
                        )
                    }
                }

                if (onExportCsv != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x2638BDF8)) // Sky blue glass
                            .border(1.dp, Color(0x6638BDF8), RoundedCornerShape(10.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(color = Color.White)
                            ) {
                                HapticFeedbackManager.performButtonClick(context, haptic)
                                onExportCsv()
                                onDismiss()
                            }
                            .testTag("dialog_export_csv_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "📊 CSV ফাইল এক্সপোর্ট (Excel / Sheets)",
                            color = Color(0xFFBAE6FD),
                            fontFamily = HindSiliguriFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 6. Close / Cancel Button
                Text(
                    text = "বন্ধ করুন",
                    color = TextMuted,
                    fontFamily = HindSiliguriFontFamily,
                    fontSize = 13.5.sp,
                    modifier = Modifier
                        .clickable { onDismiss() }
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .testTag("dialog_close_share_button")
                )
            }
        }
    }
}
