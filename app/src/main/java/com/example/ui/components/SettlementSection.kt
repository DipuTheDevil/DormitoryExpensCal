package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CalculationResult
import com.example.data.model.SettlementItem
import com.example.data.model.SettlementStatus
import com.example.ui.theme.AppTheme
import com.example.ui.theme.HindSiliguriFontFamily
import com.example.utils.BengaliFormatter
import com.example.utils.HapticFeedbackManager

@Composable
fun SettlementSection(
    calculation: CalculationResult,
    modifier: Modifier = Modifier,
    onShare: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val colors = AppTheme.colors

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(colors.resultsSectionBg)
            .border(1.dp, colors.resultsSectionBorder, RoundedCornerShape(14.dp))
            .padding(16.dp)
            .testTag("settlement_section")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.settlement_heading),
                    color = colors.textMain,
                    fontFamily = HindSiliguriFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.5.sp,
                    lineHeight = 22.sp
                )

                if (calculation.hasCalculated && calculation.settlements.isNotEmpty() && onShare != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(colors.tagGetBg)
                            .border(1.dp, Color(0x4010B981), RoundedCornerShape(8.dp))
                            .clickable {
                                HapticFeedbackManager.performButtonClick(context, haptic)
                                onShare()
                            }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                            .testTag("settlement_header_share_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.btn_share_whatsapp),
                            color = colors.tagGetText,
                            fontFamily = HindSiliguriFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (!calculation.hasCalculated || calculation.settlements.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.settlement_empty_hint),
                        color = colors.textMuted,
                        fontFamily = HindSiliguriFontFamily,
                        fontSize = 13.5.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    calculation.settlements.forEachIndexed { index, item ->
                        SettlementItemRow(item = item, index = index)
                    }

                    if (onShare != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(colors.tagGetBg)
                                .border(1.dp, Color(0x6610B981), RoundedCornerShape(10.dp))
                            .clickable {
                                HapticFeedbackManager.performButtonClick(context, haptic)
                                onShare()
                            }
                            .padding(vertical = 10.dp)
                            .testTag("settlement_bottom_share_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "📲 হিসাবটি হোয়াটসঅ্যাপ বা অন্যান্য অ্যাপে শেয়ার করুন",
                            color = colors.tagGetText,
                            fontFamily = HindSiliguriFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp
                        )
                    }
                }
            }
        }
    }
}
}

@Composable
private fun SettlementItemRow(
    item: SettlementItem,
    index: Int,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(colors.settlementItemBg)
            .border(1.dp, colors.settlementItemBorder, RoundedCornerShape(8.dp))
            .padding(horizontal = 14.dp, vertical = 9.dp)
            .testTag("settlement_item_$index"),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.displayName,
            color = colors.textMain,
            fontFamily = HindSiliguriFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            modifier = Modifier.weight(1f, fill = false)
        )

        StatusBadge(status = item.status, amount = item.amount)
    }
}

@Composable
private fun StatusBadge(
    status: SettlementStatus,
    amount: Double,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors

    val (bgColor, textColor, text) = when (status) {
        SettlementStatus.GET -> {
            val amountStr = BengaliFormatter.formatFixedTwo(amount)
            Triple(colors.tagGetBg, colors.tagGetText, stringResource(R.string.tag_get, amountStr))
        }
        SettlementStatus.GIVE -> {
            val amountStr = BengaliFormatter.formatFixedTwo(amount)
            Triple(colors.tagGiveBg, colors.tagGiveText, stringResource(R.string.tag_give, amountStr))
        }
        SettlementStatus.EVEN -> {
            Triple(colors.tagEvenBg, colors.tagEvenText, stringResource(R.string.tag_even))
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontFamily = HindSiliguriFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.5.sp,
            lineHeight = 16.sp
        )
    }
}
