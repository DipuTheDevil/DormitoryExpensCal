package com.example.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.data.model.CalculationResult

/**
 * Backward-compatible wrapper delegating to [SummaryDashboard].
 */
@Composable
fun SummaryCardsSection(
    calculation: CalculationResult,
    modifier: Modifier = Modifier
) {
    SummaryDashboard(
        calculation = calculation,
        modifier = modifier
    )
}
