package com.example.data.model

/**
 * Domain model representing a saved calculation session snapshot.
 */
data class CalculationSession(
    val id: String,
    val timestamp: Long,
    val formattedDate: String,
    val totalMembers: Int,
    val totalExpense: Double,
    val perPersonExpense: Double,
    val settlements: List<SettlementItem>,
    val members: List<Member>,
    val note: String = ""
)
