package com.example.data.model

data class CalculationResult(
    val totalExpense: Double = 0.0,
    val totalMembers: Int = 0,
    val perPersonExpense: Double = 0.0,
    val settlements: List<SettlementItem> = emptyList(),
    val hasCalculated: Boolean = false
)
