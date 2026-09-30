package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.HindSiliguriFontFamily
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.PrintBtnBg
import com.example.ui.theme.PrintBtnBorder
import com.example.ui.theme.PrintBtnText
import com.example.ui.theme.SecondaryBtnBg
import com.example.ui.theme.SecondaryBtnBorder
import com.example.ui.theme.TextMain
import com.example.utils.HapticFeedbackManager

@Composable
fun ControlsSection(
    onAddMember: () -> Unit,
    onPrintPdf: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
    onShare: (() -> Unit)? = null,
    onViewHistory: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Add member button
        Box(
            modifier = Modifier
                .heightIn(min = 44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(PrimaryIndigo)
                .clickable {
                    HapticFeedbackManager.performButtonClick(context, haptic)
                    onAddMember()
                }
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .testTag("add_member_button"),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.btn_add_member),
                color = Color.White,
                fontFamily = HindSiliguriFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }

        // 2. History button (Logs & Saved Sessions)
        if (onViewHistory != null) {
            Box(
                modifier = Modifier
                    .heightIn(min = 44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0x266366F1)) // 15% Indigo glass
                    .border(1.dp, Color(0x66818CF8), RoundedCornerShape(10.dp))
                    .clickable {
                        HapticFeedbackManager.performButtonClick(context, haptic)
                        onViewHistory()
                    }
                    .padding(horizontal = 14.dp, vertical = 10.dp)
                    .testTag("history_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📜 পূর্বের হিসাব",
                    color = Color(0xFFA5B4FC), // Indigo-tinted soft text
                    fontFamily = HindSiliguriFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
        }

        // 3. Share button (WhatsApp & messaging)
        if (onShare != null) {
            Box(
                modifier = Modifier
                    .heightIn(min = 44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0x2610B981)) // 15% emerald green glass
                    .border(1.dp, Color(0x6610B981), RoundedCornerShape(10.dp))
                    .clickable {
                        HapticFeedbackManager.performButtonClick(context, haptic)
                        onShare()
                    }
                    .padding(horizontal = 14.dp, vertical = 10.dp)
                    .testTag("share_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.btn_share),
                    color = Color(0xFFA7F3D0), // Soft green text
                    fontFamily = HindSiliguriFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
        }

        // 4. Print/PDF button
        Box(
            modifier = Modifier
                .heightIn(min = 44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(PrintBtnBg)
                .border(1.dp, PrintBtnBorder, RoundedCornerShape(10.dp))
                .clickable {
                    HapticFeedbackManager.performButtonClick(context, haptic)
                    onPrintPdf()
                }
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .testTag("print_pdf_button"),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.btn_print_pdf),
                color = PrintBtnText,
                fontFamily = HindSiliguriFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }

        // 5. Reset button
        Box(
            modifier = Modifier
                .heightIn(min = 44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(SecondaryBtnBg)
                .border(1.dp, SecondaryBtnBorder, RoundedCornerShape(10.dp))
                .clickable {
                    HapticFeedbackManager.performButtonClick(context, haptic)
                    onReset()
                }
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .testTag("reset_button"),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.btn_reset),
                color = TextMain,
                fontFamily = HindSiliguriFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }
    }
}
