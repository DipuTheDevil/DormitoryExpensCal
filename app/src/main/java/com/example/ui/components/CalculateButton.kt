package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.CalcGreenEnd
import com.example.ui.theme.CalcGreenStart
import com.example.ui.theme.HindSiliguriFontFamily
import com.example.utils.HapticFeedbackManager

@Composable
fun CalculateButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    val calcGradient = Brush.linearGradient(
        colors = listOf(CalcGreenStart, CalcGreenEnd)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(12.dp), spotColor = CalcGreenStart)
            .clip(RoundedCornerShape(12.dp))
            .background(calcGradient)
            .clickable {
                HapticFeedbackManager.performCalculateAction(context, haptic)
                onClick()
            }
            .padding(vertical = 12.dp, horizontal = 24.dp)
            .heightIn(min = 48.dp)
            .testTag("calculate_button"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.btn_calculate),
            color = Color.White,
            fontFamily = HindSiliguriFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}
