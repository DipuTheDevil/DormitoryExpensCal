package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.ui.components.CalculateButton
import com.example.ui.components.ControlsSection
import com.example.ui.components.FooterSection
import com.example.ui.components.HeaderSection
import com.example.ui.components.HistoryDialog
import com.example.ui.components.MemberManagementSection
import com.example.ui.components.ResetConfirmDialog
import com.example.ui.components.SettlementSection
import com.example.ui.components.ShareSummaryDialog
import com.example.ui.components.SummaryDashboard
import com.example.ui.navigation.AppRoute
import com.example.ui.theme.AppTheme
import com.example.ui.viewmodel.MessViewModel

/**
 * Main Calculator screen following responsive, adaptive layout patterns.
 * Supports theme toggling (Light by default) and HashRouter navigation.
 */
@Composable
fun MessCalculatorScreen(
    viewModel: MessViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val colors = AppTheme.colors

    val screenBackgroundBrush = Brush.linearGradient(
        colors = listOf(colors.bgGradientStart, colors.bgGradientEnd)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(screenBackgroundBrush)
            .imePadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            contentAlignment = Alignment.TopCenter
        ) {
            val isSmallScreen = maxWidth < 480.dp
            val outerPadding = if (isSmallScreen) 12.dp else 24.dp
            val innerPadding = if (isSmallScreen) 16.dp else 28.dp

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(outerPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Card Container (.container)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 850.dp)
                        .shadow(
                            elevation = if (colors.isDark) 16.dp else 6.dp,
                            shape = RoundedCornerShape(24.dp),
                            ambientColor = if (colors.isDark) colors.bgGradientStart else Color(0x33000000),
                            spotColor = if (colors.isDark) colors.bgGradientStart else Color(0x26000000)
                        )
                        .clip(RoundedCornerShape(24.dp))
                        .background(colors.cardBackground)
                        .border(1.dp, colors.cardBorder, RoundedCornerShape(24.dp))
                        .padding(innerPadding)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 1. Header with theme toggle & history navigation (#history)
                        HeaderSection(
                            isDarkTheme = uiState.isDarkTheme,
                            historyCount = uiState.sessions.size,
                            onToggleTheme = { viewModel.toggleTheme() },
                            onNavigateToHistory = { viewModel.navigateTo(AppRoute.HISTORY) }
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // 2. Control buttons
                        ControlsSection(
                            onAddMember = { viewModel.addMember() },
                            onPrintPdf = { viewModel.printOrSavePdf(context) },
                            onReset = { viewModel.showResetDialog() },
                            onShare = { viewModel.shareSettlement(context) },
                            onViewHistory = { viewModel.navigateTo(AppRoute.HISTORY) }
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // 3. Member list
                        MemberManagementSection(
                            members = uiState.members,
                            onNameChange = { index, newName ->
                                viewModel.updateMemberName(index, newName)
                            },
                            onExpenseChange = { index, newExpense ->
                                viewModel.updateMemberExpense(index, newExpense)
                            },
                            onDeleteMember = { index ->
                                viewModel.removeMember(index)
                            }
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // 4. Calculate button
                        CalculateButton(
                            onClick = { viewModel.calculate() }
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // 5. Summary dashboard
                        SummaryDashboard(
                            calculation = uiState.calculation
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // 6. Results / Settlement section
                        SettlementSection(
                            calculation = uiState.calculation,
                            onShare = { viewModel.shareSettlement(context) }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // 7. Footer
                        FooterSection()
                    }
                }
            }
        }

        // Reset Confirmation Dialog
        if (uiState.showResetDialog) {
            ResetConfirmDialog(
                onConfirm = { viewModel.confirmReset() },
                onDismiss = { viewModel.dismissResetDialog() }
            )
        }

        // Share Summary Dialog
        if (uiState.showShareDialog) {
            ShareSummaryDialog(
                summaryText = uiState.shareText,
                onDismiss = { viewModel.dismissShareDialog() }
            )
        }

        // Calculation History Dialog (Quick modal access)
        if (uiState.showHistoryDialog) {
            HistoryDialog(
                sessions = uiState.sessions,
                onDismiss = { viewModel.dismissHistoryDialog() },
                onRestoreSession = { session -> viewModel.restoreSession(session) },
                onDeleteSession = { sessionId -> viewModel.deleteSession(sessionId) },
                onClearAll = { viewModel.clearAllHistory() }
            )
        }
    }
}
