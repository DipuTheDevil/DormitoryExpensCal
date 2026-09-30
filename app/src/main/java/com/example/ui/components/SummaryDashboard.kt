package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CalculationResult
import com.example.domain.logic.ExpenseSummary
import com.example.ui.theme.AppTheme
import com.example.ui.theme.HindSiliguriFontFamily
import com.example.utils.BengaliFormatter
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * 'SummaryDashboard' component displaying the three metric cards:
 * 1. 'মোট খরচ'
 * 2. 'মোট সদস্য'
 * 3. 'মাথাপিছু খরচ'
 *
 * Adapts dynamically to Light and Dark theme with Bengali numerals and '৳' currency symbols.
 */
@Composable
fun SummaryDashboard(
    calculation: CalculationResult,
    modifier: Modifier = Modifier
) {
    val totalExpenseStr = if (calculation.hasCalculated) {
        formatCurrencyBengali(calculation.totalExpense)
    } else {
        "৳ ০"
    }

    val totalMembersStr = if (calculation.hasCalculated) {
        BengaliFormatter.toBengaliDigits(calculation.totalMembers)
    } else {
        "০"
    }

    val perPersonStr = if (calculation.hasCalculated) {
        formatCurrencyBengali(calculation.perPersonExpense)
    } else {
        "৳ ০"
    }

    SummaryDashboardLayout(
        totalExpense = totalExpenseStr,
        totalMembers = totalMembersStr,
        perPersonExpense = perPersonStr,
        modifier = modifier
    )
}

/**
 * Overload accepting domain [ExpenseSummary].
 */
@Composable
fun SummaryDashboard(
    summary: ExpenseSummary,
    modifier: Modifier = Modifier
) {
    SummaryDashboardLayout(
        totalExpense = summary.formattedTotal,
        totalMembers = BengaliFormatter.toBengaliDigits(summary.memberCount),
        perPersonExpense = summary.formattedPerPerson,
        modifier = modifier
    )
}

/**
 * Overload accepting direct numerical values.
 */
@Composable
fun SummaryDashboard(
    totalExpense: BigDecimal,
    totalMembers: Int,
    perPersonExpense: BigDecimal,
    modifier: Modifier = Modifier
) {
    SummaryDashboardLayout(
        totalExpense = formatCurrencyBengali(totalExpense),
        totalMembers = BengaliFormatter.toBengaliDigits(totalMembers),
        perPersonExpense = formatCurrencyBengali(perPersonExpense),
        modifier = modifier
    )
}

@Composable
private fun SummaryDashboardLayout(
    totalExpense: String,
    totalMembers: String,
    perPersonExpense: String,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .testTag("summary_dashboard")
    ) {
        val isNarrow = maxWidth < 420.dp

        if (isNarrow) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = stringResource(R.string.summary_total_expense),
                        value = totalExpense,
                        valueColor = colors.summaryExpenseColor,
                        modifier = Modifier.weight(1f),
                        testTag = "summary_total_expense"
                    )
                    MetricCard(
                        title = stringResource(R.string.summary_total_members),
                        value = totalMembers,
                        valueColor = colors.textMain,
                        modifier = Modifier.weight(1f),
                        testTag = "summary_total_members"
                    )
                }
                MetricCard(
                    title = stringResource(R.string.summary_per_person),
                    value = perPersonExpense,
                    valueColor = colors.summaryPerPersonColor,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "summary_per_person"
                )
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = stringResource(R.string.summary_total_expense),
                    value = totalExpense,
                    valueColor = colors.summaryExpenseColor,
                    modifier = Modifier.weight(1f),
                    testTag = "summary_total_expense"
                )
                MetricCard(
                    title = stringResource(R.string.summary_total_members),
                    value = totalMembers,
                    valueColor = colors.textMain,
                    modifier = Modifier.weight(0.9f),
                    testTag = "summary_total_members"
                )
                MetricCard(
                    title = stringResource(R.string.summary_per_person),
                    value = perPersonExpense,
                    valueColor = colors.summaryPerPersonColor,
                    modifier = Modifier.weight(1.2f),
                    testTag = "summary_per_person"
                )
            }
        }
    }
}

/**
 * Reusable metric card adapting to theme.
 */
@Composable
private fun MetricCard(
    title: String,
    value: String,
    valueColor: Color,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors

    Box(
        modifier = modifier
            .shadow(
                elevation = if (colors.isDark) 4.dp else 1.5.dp,
                shape = RoundedCornerShape(10.dp),
                ambientColor = Color(0x33000000),
                spotColor = Color(0x26000000)
            )
            .clip(RoundedCornerShape(10.dp))
            .background(colors.cardRowBackground)
            .border(1.dp, colors.cardRowBorder, RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 12.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = title,
                color = colors.textMuted,
                fontFamily = HindSiliguriFontFamily,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                color = valueColor,
                fontFamily = HindSiliguriFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private fun formatCurrencyBengali(amount: Double): String {
    val formatted = BengaliFormatter.formatCurrency(amount)
    return "৳ $formatted"
}

private fun formatCurrencyBengali(amount: BigDecimal): String {
    val scaled = amount.setScale(2, RoundingMode.HALF_UP)
    val df = DecimalFormat("#,##,##0.00", DecimalFormatSymbols(Locale.US))
    val bengali = BengaliFormatter.toBengaliDigits(df.format(scaled))
    return "৳ $bengali"
}
