package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.AppTheme
import com.example.ui.theme.HindSiliguriFontFamily

@Composable
fun FooterSection(modifier: Modifier = Modifier) {
    val colors = AppTheme.colors

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalDivider(
            color = colors.cardBorder,
            thickness = 1.dp
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = stringResource(R.string.footer_text),
            color = colors.textMuted,
            fontFamily = HindSiliguriFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            letterSpacing = 0.5.sp
        )
    }
}
