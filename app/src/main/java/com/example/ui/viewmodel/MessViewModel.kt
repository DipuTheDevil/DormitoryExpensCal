package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.CalculationResult
import com.example.data.model.CalculationSession
import com.example.data.model.Member
import com.example.data.repository.MessExpenseRepository
import com.example.domain.ExpenseCalculator
import com.example.ui.navigation.AppRoute
import com.example.utils.PrintReportHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MessUiState(
    val currentRoute: AppRoute = AppRoute.CALCULATOR,
    val isDarkTheme: Boolean = false, // Light theme is default
    val members: List<Member> = listOf(Member()),
    val calculation: CalculationResult = CalculationResult(),
    val sessions: List<CalculationSession> = emptyList(),
    val showResetDialog: Boolean = false,
    val showShareDialog: Boolean = false,
    val showHistoryDialog: Boolean = false,
    val shareText: String = "",
    val isLoaded: Boolean = false
)

class MessViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MessExpenseRepository(application)

    private val _uiState = MutableStateFlow(MessUiState())
    val uiState: StateFlow<MessUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
        observeSessions()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val savedMembers = repository.membersFlow.first()
            val list = if (savedMembers.isEmpty()) listOf(Member()) else savedMembers
            _uiState.update { it.copy(members = list, isLoaded = true) }
        }
    }

    private fun observeSessions() {
        viewModelScope.launch {
            repository.sessionsFlow.collect { sessionsList ->
                _uiState.update { it.copy(sessions = sessionsList) }
            }
        }
    }

    fun addMember() {
        val current = _uiState.value.members.toMutableList()
        current.add(Member(name = "", expense = ""))
        updateMembersAndSave(current)
    }

    fun removeMember(index: Int) {
        val current = _uiState.value.members.toMutableList()
        if (index in current.indices && current.size > 1) {
            current.removeAt(index)
            updateMembersAndSave(current)
            if (_uiState.value.calculation.hasCalculated) {
                calculate()
            }
        }
    }

    fun updateMemberName(index: Int, name: String) {
        val current = _uiState.value.members.toMutableList()
        if (index in current.indices) {
            current[index] = current[index].copy(name = name)
            updateMembersAndSave(current)
        }
    }

    fun updateMemberExpense(index: Int, expense: String) {
        val current = _uiState.value.members.toMutableList()
        if (index in current.indices) {
            current[index] = current[index].copy(expense = expense)
            updateMembersAndSave(current)
        }
    }

    fun calculate() {
        val currentMembers = _uiState.value.members
        val result = ExpenseCalculator.calculate(currentMembers)
        _uiState.update { it.copy(calculation = result) }

        // Automatically log snapshot in local database if calculation is valid
        if (result.hasCalculated && result.settlements.isNotEmpty()) {
            viewModelScope.launch {
                repository.saveSession(result, currentMembers)
            }
        }
    }

    fun showResetDialog() {
        _uiState.update { it.copy(showResetDialog = true) }
    }

    fun dismissResetDialog() {
        _uiState.update { it.copy(showResetDialog = false) }
    }

    fun confirmReset() {
        val resetList = listOf(
            Member(name = "", expense = ""),
            Member(name = "", expense = ""),
            Member(name = "", expense = "")
        )
        viewModelScope.launch {
            repository.clearData()
            _uiState.update {
                it.copy(
                    members = resetList,
                    calculation = CalculationResult(),
                    showResetDialog = false
                )
            }
        }
    }

    fun printOrSavePdf(context: Context) {
        val members = _uiState.value.members
        val calc = if (_uiState.value.calculation.hasCalculated) {
            _uiState.value.calculation
        } else {
            ExpenseCalculator.calculate(members)
        }
        PrintReportHelper.printReport(context, members, calc)
    }

    fun shareSettlement(context: Context) {
        val members = _uiState.value.members
        val calc = if (_uiState.value.calculation.hasCalculated) {
            _uiState.value.calculation
        } else {
            val calculated = ExpenseCalculator.calculate(members)
            _uiState.update { it.copy(calculation = calculated) }
            calculated
        }
        val text = com.example.utils.ShareReportHelper.generateSummaryText(members, calc)
        _uiState.update { it.copy(showShareDialog = true, shareText = text) }
    }

    fun dismissShareDialog() {
        _uiState.update { it.copy(showShareDialog = false) }
    }

    fun openHistoryDialog() {
        _uiState.update { it.copy(showHistoryDialog = true) }
    }

    fun dismissHistoryDialog() {
        _uiState.update { it.copy(showHistoryDialog = false) }
    }

    fun navigateTo(route: AppRoute) {
        _uiState.update { it.copy(currentRoute = route) }
    }

    fun navigateToHash(hash: String) {
        val route = AppRoute.fromHash(hash)
        _uiState.update { it.copy(currentRoute = route) }
    }

    fun toggleTheme() {
        _uiState.update { it.copy(isDarkTheme = !it.isDarkTheme) }
    }

    fun setDarkTheme(enabled: Boolean) {
        _uiState.update { it.copy(isDarkTheme = enabled) }
    }

    fun restoreSession(session: CalculationSession) {
        val restoredMembers = session.members.map { it.copy() }
        updateMembersAndSave(restoredMembers)
        val result = ExpenseCalculator.calculate(restoredMembers)
        _uiState.update {
            it.copy(
                calculation = result,
                showHistoryDialog = false,
                currentRoute = AppRoute.CALCULATOR
            )
        }
    }

    fun deleteSession(sessionId: String) {
        viewModelScope.launch {
            repository.deleteSession(sessionId)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearAllSessions()
        }
    }

    private fun updateMembersAndSave(newList: List<Member>) {
        _uiState.update { it.copy(members = newList) }
        viewModelScope.launch {
            repository.saveMembers(newList)
        }
    }
}
