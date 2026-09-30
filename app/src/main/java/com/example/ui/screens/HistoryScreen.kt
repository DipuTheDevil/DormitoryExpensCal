package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.CalculationSession
import com.example.data.model.SettlementStatus
import com.example.ui.navigation.AppRoute
import com.example.ui.theme.AppTheme
import com.example.ui.theme.HindSiliguriFontFamily
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.viewmodel.MessViewModel
import com.example.utils.BengaliFormatter
import com.example.utils.HapticFeedbackManager
import com.example.utils.ShareReportHelper
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

@Composable
fun HistoryScreen(
    viewModel: MessViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    // Handle Android system back button to return to calculator screen
    BackHandler {
        viewModel.navigateTo(AppRoute.CALCULATOR)
    }

    var searchQuery by remember { mutableStateOf("") }
    var showClearConfirmDialog by remember { mutableStateOf(false) }

    val colors = AppTheme.colors

    val bgGradient = Brush.verticalGradient(
        colors = listOf(colors.bgGradientStart, colors.bgGradientEnd)
    )

    val filteredSessions = remember(uiState.sessions, searchQuery) {
        if (searchQuery.isBlank()) {
            uiState.sessions
        } else {
            val q = searchQuery.trim().lowercase(Locale.ROOT)
            uiState.sessions.filter { session ->
                session.formattedDate.lowercase(Locale.ROOT).contains(q) ||
                        session.members.any { it.name.lowercase(Locale.ROOT).contains(q) } ||
                        session.settlements.any { it.displayName.lowercase(Locale.ROOT).contains(q) }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .widthIn(max = 600.dp)
                .align(Alignment.TopCenter)
        ) {
            // 1. Top Bar with Back Button, Title, and Theme Switcher
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back to Calculator
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(colors.secondaryBtnBg)
                        .border(1.dp, colors.secondaryBtnBorder, RoundedCornerShape(10.dp))
                        .clickable {
                            HapticFeedbackManager.performButtonClick(context, haptic)
                            viewModel.navigateTo(AppRoute.CALCULATOR)
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .testTag("history_back_button"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⬅️",
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ক্যালকুলেটর",
                        color = colors.textMain,
                        fontFamily = HindSiliguriFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }

                // Title
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "📜 পূর্বের হিসাব",
                        color = colors.textMain,
                        fontFamily = HindSiliguriFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0x264F46E5))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = BengaliFormatter.toBengaliDigits(uiState.sessions.size),
                            color = PrimaryIndigo,
                            fontFamily = HindSiliguriFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                // Theme Toggle Button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(colors.secondaryBtnBg)
                        .border(1.dp, colors.secondaryBtnBorder, CircleShape)
                        .clickable {
                            HapticFeedbackManager.performButtonClick(context, haptic)
                            viewModel.toggleTheme()
                        }
                        .testTag("theme_toggle_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (uiState.isDarkTheme) "☀️" else "🌙",
                        fontSize = 18.sp
                    )
                }
            }

            // 2. Search & Filter Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.inputBackground)
                    .border(1.dp, colors.inputBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🔍", fontSize = 15.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("history_search_input"),
                        textStyle = TextStyle(
                            fontFamily = HindSiliguriFontFamily,
                            fontSize = 14.sp,
                            color = colors.textMain
                        ),
                        singleLine = true,
                        cursorBrush = SolidColor(PrimaryIndigo),
                        decorationBox = { innerTextField ->
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = "সদস্যের নাম বা তারিখ দিয়ে খুঁজুন...",
                                    color = colors.textMuted,
                                    fontFamily = HindSiliguriFontFamily,
                                    fontSize = 13.5.sp
                                )
                            }
                            innerTextField()
                        }
                    )
                    if (searchQuery.isNotEmpty()) {
                        Text(
                            text = "✕",
                            color = colors.textMuted,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { searchQuery = "" }
                                .padding(4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3. Clear all option (if sessions exist)
            if (uiState.sessions.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "মোট সংরক্ষিত সেশন: ${BengaliFormatter.toBengaliDigits(filteredSessions.size)} টি",
                        color = colors.textMuted,
                        fontFamily = HindSiliguriFontFamily,
                        fontSize = 12.5.sp
                    )

                    Text(
                        text = "🗑️ সব ইতিহাস মুছুন",
                        color = Color(0xFFEF4444),
                        fontFamily = HindSiliguriFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.5.sp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { showClearConfirmDialog = true }
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                            .testTag("clear_all_history_screen_button")
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
            }

            // 4. Session List or Empty State
            if (filteredSessions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(text = "📭", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) "কোনো ফলাফল পাওয়া যায়নি" else "এখনো কোনো পূর্ববর্তী হিসাব সংরক্ষিত নেই",
                            color = colors.textMain,
                            fontFamily = HindSiliguriFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) "ভিন্ন কোনো নাম বা তারিখ দিয়ে সার্চ করে দেখুন।" else "ক্যালকুলেটর পেজে 'হিসাব করুন' বাটনে চাপলে অটোমেটিক এখানে সংরক্ষিত হবে।",
                            color = colors.textMuted,
                            fontFamily = HindSiliguriFontFamily,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredSessions, key = { it.id }) { session ->
                        HistorySessionListItem(
                            session = session,
                            onRestore = {
                                HapticFeedbackManager.performButtonClick(context, haptic)
                                viewModel.restoreSession(session)
                            },
                            onShare = {
                                HapticFeedbackManager.performButtonClick(context, haptic)
                                shareHistorySession(context, session)
                            },
                            onDelete = {
                                HapticFeedbackManager.performDeleteAction(context, haptic)
                                viewModel.deleteSession(session.id)
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }

    // Confirmation dialog for clearing all history
    if (showClearConfirmDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = {
                Text(
                    text = "সব ইতিহাস মুছে ফেলতে চান?",
                    fontFamily = HindSiliguriFontFamily,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "সংরক্ষিত সকল পূর্ববর্তী হিসাব স্থায়ীভাবে মুছে যাবে। এই কাজটি আর ফেরানো যাবে না।",
                    fontFamily = HindSiliguriFontFamily
                )
            },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        viewModel.clearAllHistory()
                        showClearConfirmDialog = false
                    }
                ) {
                    Text("হ্যাঁ, সব মুছুন", color = Color(0xFFEF4444), fontFamily = HindSiliguriFontFamily, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(
                    onClick = { showClearConfirmDialog = false }
                ) {
                    Text("বাতিল", fontFamily = HindSiliguriFontFamily)
                }
            }
        )
    }
}

@Composable
private fun HistorySessionListItem(
    session: CalculationSession,
    onRestore: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val colors = AppTheme.colors

    val df = remember { DecimalFormat("#,##,##0.00", DecimalFormatSymbols(Locale.US)) }
    val totalExpenseFormatted = BengaliFormatter.toBengaliDigits(df.format(session.totalExpense))
    val perPersonFormatted = BengaliFormatter.toBengaliDigits(df.format(session.perPersonExpense))
    val membersCountFormatted = BengaliFormatter.toBengaliDigits(session.totalMembers)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (colors.isDark) 4.dp else 2.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color(0x33000000)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(colors.cardBackground)
            .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp))
            .padding(14.dp)
            .testTag("history_screen_item_${session.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header: Date & Delete icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "📅", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = session.formattedDate,
                        color = colors.textMain,
                        fontFamily = HindSiliguriFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp
                    )
                }

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
                        color = Color(0xFFEF4444),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Stats grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(colors.cardRowBackground)
                    .border(1.dp, colors.cardRowBorder, RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "মোট সদস্য", color = colors.textMuted, fontSize = 11.sp, fontFamily = HindSiliguriFontFamily)
                    Text(
                        text = "$membersCountFormatted জন",
                        color = colors.textMain,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        fontFamily = HindSiliguriFontFamily
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "মোট খরচ", color = colors.textMuted, fontSize = 11.sp, fontFamily = HindSiliguriFontFamily)
                    Text(
                        text = "৳ $totalExpenseFormatted",
                        color = colors.summaryExpenseColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        fontFamily = HindSiliguriFontFamily
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "মাথাপিছু ভাগ", color = colors.textMuted, fontSize = 11.sp, fontFamily = HindSiliguriFontFamily)
                    Text(
                        text = "৳ $perPersonFormatted",
                        color = colors.summaryPerPersonColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        fontFamily = HindSiliguriFontFamily
                    )
                }
            }

            // Expandable settlements breakdown
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
                        color = colors.textMuted,
                        fontFamily = HindSiliguriFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )

                    session.settlements.forEach { item ->
                        val amountStr = BengaliFormatter.toBengaliDigits(df.format(item.amount))
                        val (bg, textColor, tagText) = when (item.status) {
                            SettlementStatus.GET -> Triple(colors.tagGetBg, colors.tagGetText, "ফেরত পাবেন ৳ $amountStr")
                            SettlementStatus.GIVE -> Triple(colors.tagGiveBg, colors.tagGiveText, "দিবেন ৳ $amountStr")
                            SettlementStatus.EVEN -> Triple(colors.tagEvenBg, colors.tagEvenText, "হিসাব সমান")
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(colors.settlementItemBg)
                                .border(1.dp, colors.settlementItemBorder, RoundedCornerShape(6.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.displayName,
                                color = colors.textMain,
                                fontFamily = HindSiliguriFontFamily,
                                fontSize = 13.sp,
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
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons: Details toggle, Restore, Share
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Toggle details
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(colors.secondaryBtnBg)
                        .border(1.dp, colors.secondaryBtnBorder, RoundedCornerShape(8.dp))
                        .clickable { expanded = !expanded },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (expanded) "সংক্ষেপ ▴" else "বিস্তারিত ▾",
                        color = colors.secondaryBtnText,
                        fontFamily = HindSiliguriFontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Restore
                Box(
                    modifier = Modifier
                        .weight(1.2f)
                        .height(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(PrimaryIndigo)
                        .clickable { onRestore() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📥 রিস্টোর করুন",
                        color = Color.White,
                        fontFamily = HindSiliguriFontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Share
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(colors.tagGetBg)
                        .border(1.dp, Color(0x3310B981), RoundedCornerShape(8.dp))
                        .clickable { onShare() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📲 শেয়ার",
                        color = colors.tagGetText,
                        fontFamily = HindSiliguriFontFamily,
                        fontSize = 12.sp,
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
