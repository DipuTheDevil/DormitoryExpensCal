package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.AppTheme
import com.example.ui.theme.HindSiliguriFontFamily
import com.example.ui.theme.PrimaryIndigo
import com.example.utils.BengaliFormatter
import com.example.utils.HapticFeedbackManager

@Composable
fun HeaderSection(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = false,
    historyCount: Int = 0,
    onToggleTheme: (() -> Unit)? = null,
    onNavigateToHistory: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val colors = AppTheme.colors

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Action Bar: Navigation to History (#history) & Theme Toggle Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // History Nav Button (#history)
            if (onNavigateToHistory != null) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(colors.secondaryBtnBg)
                        .border(1.dp, colors.secondaryBtnBorder, RoundedCornerShape(20.dp))
                        .clickable {
                            HapticFeedbackManager.performButtonClick(context, haptic)
                            onNavigateToHistory()
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("nav_to_history_button"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "📜", fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "পূর্বের হিসাব",
                        color = colors.textMain,
                        fontFamily = HindSiliguriFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.5.sp
                    )
                    if (historyCount > 0) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(PrimaryIndigo)
                                .padding(horizontal = 6.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = BengaliFormatter.toBengaliDigits(historyCount),
                                color = Color.White,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }

            // Theme Toggle Button (Light/Dark mode)
            if (onToggleTheme != null) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(colors.secondaryBtnBg)
                        .border(1.dp, colors.secondaryBtnBorder, RoundedCornerShape(20.dp))
                        .clickable {
                            HapticFeedbackManager.performButtonClick(context, haptic)
                            onToggleTheme()
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("theme_toggle_button"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isDarkTheme) "☀️" else "🌙",
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isDarkTheme) "লাইট মোড" else "ডার্ক মোড",
                        color = colors.textMain,
                        fontFamily = HindSiliguriFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.5.sp
                    )
                }
            }
        }

        // App Icon Emblem
        Box(
            modifier = Modifier
                .size(62.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = Color(0x664F46E5),
                    ambientColor = Color(0x33000000)
                )
                .clip(RoundedCornerShape(16.dp))
                .border(1.5.dp, Color(0x40818CF8), RoundedCornerShape(16.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.mess_calculator_icon_1790768091219),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        val titleGradient = Brush.horizontalGradient(
            colors = listOf(colors.titleGradientStart, colors.titleGradientEnd)
        )

        Text(
            text = stringResource(R.string.header_title),
            style = TextStyle(
                fontFamily = HindSiliguriFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                lineHeight = 32.sp,
                brush = titleGradient,
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = stringResource(R.string.app_subtitle),
            color = colors.textMuted,
            fontFamily = HindSiliguriFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 13.5.sp,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )
    }
}
