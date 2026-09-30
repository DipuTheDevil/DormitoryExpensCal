package com.example.data.model

enum class SettlementStatus {
    GET,   // ফেরত পাবেন
    GIVE,  // দিবেন
    EVEN   // হিসাব সমতা
}

data class SettlementItem(
    val memberId: String,
    val displayName: String,
    val status: SettlementStatus,
    val amount: Double
)
