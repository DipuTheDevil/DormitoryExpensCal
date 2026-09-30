package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.CalculationSession
import com.example.data.model.SettlementStatus
import com.example.ui.theme.CardBorder
import com.example.ui.theme.HindSiliguriFontFamily
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.TagEvenBg
import com.example.ui.theme.TagEvenText
import com.example.ui.theme.TagGetBg
import com.example.ui.theme.TagGetText
import com.example.ui.theme.TagGiveBg
import com.example.ui.theme.TagGiveText
import com.example.ui.theme.TextMain
import com.example.ui.theme.TextMuted
import com.example.utils.BengaliFormatter
import com.example.utils.HapticFeedbackManager
import com.example.utils.ShareReportHelper
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryDialog(
    sessions: List<CalculationSession>,
    onDismiss: () -> Unit,
    onRestoreSession: (CalculationSession) -> Unit,
    onDeleteSession: (String) -> Unit,
    onClearAll: () -> Unit
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

        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .widthIn(max = 480.dp)
                .fillMaxWidth()
                .shadow(
                    elevation = 24.dp,
                    shape = RoundedCornerShape(20.dp),
                    ambientColor = Color(0x99000000),
                    spotColor = Color(0x99000000)
                )
                .clip(RoundedCornerShape(20.dp))
                .background(glassCardGradient)
                .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(20.dp))
                .padding(18.dp)
                .testTag("history_dialog")
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. Header with Badge & Clear All
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "📜",
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "পূর্বের হিসাব সমূহ",
                            color = Color.White,
                            fontFamily = HindSiliguriFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.5.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        // Count Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0x336366F1))
                                .border(1.dp, Color(0x4D818CF8), RoundedCornerShape(6.dp))
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = BengaliFormatter.toBengaliDigits(sessions.size),
                                color = Color(0xFFA5B4FC),
                                fontFamily = HindSiliguriFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    if (sessions.isNotEmpty()) {
                        Text(
                            text = "সব মুছুন",
                            color = Color(0xFFF87171),
                            fontFamily = HindSiliguriFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.5.sp,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable {
                                    HapticFeedbackManager.performDeleteAction(context, haptic)
                                    onClearAll()
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("clear_all_history_button")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Sessions List / Empty State
                if (sessions.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "📭",
                                fontSize = 36.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "এখনো কোনো পূর্ববর্তী হিসাব সংরক্ষিত নেই।",
                                color = Color.White,
                                fontFamily = HindSiliguriFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.5.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "'হিসাব করুন' বাটনে চাপলে অটোমেটিক স্ন্যাপশট সংরক্ষিত হয়।",
                                color = TextMuted,
                                fontFamily = HindSiliguriFontFamily,
                                fontSize = 12.5.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 380.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(sessions, key = { it.id }) { session ->
                            HistorySessionCard(
                                session = session,
                                onRestore = {
                                    HapticFeedbackManager.performButtonClick(context, haptic)
                                    onRestoreSession(session)
                                },
                                onShare = {
                                    HapticFeedbackManager.performButtonClick(context, haptic)
                                    shareHistorySession(context, session)
                                },
                                onDelete = {
                                    HapticFeedbackManager.performDeleteAction(context, haptic)
                                    onDeleteSession(session.id)
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Close Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x1AFFFFFF))
                        .border(1.dp, Color(0x26FFFFFF), RoundedCornerShape(10.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(color = Color.White)
                        ) { onDismiss() }
                        .testTag("close_history_dialog_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "বন্ধ করুন",
                        color = Color.White,
                        fontFamily = HindSiliguriFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun HistorySessionCard(
    session: CalculationSession,
    onRestore: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val df = remember { DecimalFormat("#,##,##0.00", DecimalFormatSymbols(Locale.US)) }
    val totalExpenseFormatted = BengaliFormatter.toBengaliDigits(df.format(session.totalExpense))
    val perPersonFormatted = BengaliFormatter.toBengaliDigits(df.format(session.perPersonExpense))
    val membersCountFormatted = BengaliFormatter.toBengaliDigits(session.totalMembers)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x590F172A))
            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
            .padding(12.dp)
            .testTag("history_card_${session.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Timestamp Header & Delete Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "📅", fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = session.formattedDate,
                        color = Color(0xFFA5B4FC),
                        fontFamily = HindSiliguriFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.5.sp
                    )
                }

                // Delete icon button
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0x1AEF4444))
                        .clickable { onDelete() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✕",
                        color = Color(0xFFF87171),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Stats summary
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x331E293B))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "মোট সদস্য", color = TextMuted, fontSize = 11.sp, fontFamily = HindSiliguriFontFamily)
                    Text(
                        text = "$membersCountFormatted জন",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        fontFamily = HindSiliguriFontFamily
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "মোট খরচ", color = TextMuted, fontSize = 11.sp, fontFamily = HindSiliguriFontFamily)
                    Text(
                        text = "৳ $totalExpenseFormatted",
                        color = Color(0xFF38BDF8),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        fontFamily = HindSiliguriFontFamily
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "মাথাপিছু ভাগ", color = TextMuted, fontSize = 11.sp, fontFamily = HindSiliguriFontFamily)
                    Text(
                        text = "৳ $perPersonFormatted",
                        color = Color(0xFF4ADE80),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        fontFamily = HindSiliguriFontFamily
                    )
                }
            }

            // Expandable details (settlements list)
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "দেনা-পাওনার হিসাব:",
                        color = Color(0xFFCBD5E1),
                        fontFamily = HindSiliguriFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )

                    session.settlements.forEach { item ->
                        val amountStr = BengaliFormatter.toBengaliDigits(df.format(item.amount))
                        val (bg, textColor, tagText) = when (item.status) {
                            SettlementStatus.GET -> Triple(TagGetBg, TagGetText, "ফেরত পাবেন ৳ $amountStr")
                            SettlementStatus.GIVE -> Triple(TagGiveBg, TagGiveText, "দিবেন ৳ $amountStr")
                            SettlementStatus.EVEN -> Triple(TagEvenBg, TagEvenText, "হিসাব সমান")
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0x260F172A))
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.displayName,
                                color = TextMain,
                                fontFamily = HindSiliguriFontFamily,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(bg)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = tagText,
                                    color = textColor,
                                    fontFamily = HindSiliguriFontFamily,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Card Action Buttons: Toggle Details, Restore, Share
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Details toggle
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(34.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0x1AFFFFFF))
                        .clickable { expanded = !expanded },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (expanded) "সংক্ষেপ করুন ▴" else "বিস্তারিত ▾",
                        color = Color(0xFFCBD5E1),
                        fontFamily = HindSiliguriFontFamily,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Restore Button
                Box(
                    modifier = Modifier
                        .weight(1.1f)
                        .height(34.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(PrimaryIndigo)
                        .clickable { onRestore() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📥 রিস্টোর করুন",
                        color = Color.White,
                        fontFamily = HindSiliguriFontFamily,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Share Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(34.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0x2610B981))
                        .border(1.dp, Color(0x6610B981), RoundedCornerShape(6.dp))
                        .clickable { onShare() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📲 শেয়ার",
                        color = Color(0xFFA7F3D0),
                        fontFamily = HindSiliguriFontFamily,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

private fun shareHistorySession(context: android.content.Context, session: CalculationSession) {
    val df = DecimalFormat("#,##,##0.00", DecimalFormatSymbols(Locale.US))
    val totalMembersStr = BengaliFormatter.toBengaliDigits(session.totalMembers)
    val totalExpenseStr = BengaliFormatter.toBengaliDigits(df.format(session.totalExpense))
    val perPersonStr = BengaliFormatter.toBengaliDigits(df.format(session.perPersonExpense))

    val sb = StringBuilder()
    sb.append("📊 *বাজার খরচ হিসাব বিবরণী (সংরক্ষিত হিস্ট্রি)*\n")
    sb.append("📅 তারিখ: ${session.formattedDate}\n")
    sb.append("━━━━━━━━━━━━━━━━━━━━\n")
    sb.append("👥 *মোট সদস্য:* $totalMembersStr জন\n")
    sb.append("💰 *মোট খরচ:* ৳ $totalExpenseStr\n")
    sb.append("⚖️ *মাথাপিছু সমান ভাগ:* ৳ $perPersonStr\n\n")

    sb.append("📋 *সদস্যদের খরচ:*\n")
    session.members.forEachIndexed { index, m ->
        val serial = BengaliFormatter.toBengaliDigits(index + 1)
        val name = if (m.name.trim().isNotEmpty()) m.name.trim() else "সদস্য $serial"
        val exp = BengaliFormatter.toBengaliDigits(df.format(m.numericExpense))
        sb.append("$serial. $name: ৳ $exp\n")
    }

    sb.append("\n⚖️ *দেনা-পাওনার হিসাব:*\n")
    session.settlements.forEach { item ->
        val amt = BengaliFormatter.toBengaliDigits(df.format(item.amount))
        when (item.status) {
            SettlementStatus.GET -> sb.append("• *${item.displayName}*: ফেরত পাবেন ৳ $amt 🟢\n")
            SettlementStatus.GIVE -> sb.append("• *${item.displayName}*: দিবেন ৳ $amt 🔴\n")
            SettlementStatus.EVEN -> sb.append("• *${item.displayName}*: হিসাব সমতা ⚪\n")
        }
    }
    sb.append("━━━━━━━━━━━━━━━━━━━━\n")
    sb.append("📲 হিসাব Pro - মেসের খরচ ভাগ করার স্মার্ট ক্যালকুলেটর")

    ShareReportHelper.shareText(context, sb.toString())
}
