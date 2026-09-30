package com.example.data.model

import java.util.UUID

data class Member(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val expense: String = ""
) {
    val numericExpense: Double
        get() = com.example.utils.BengaliFormatter.toAsciiDigits(expense)
            .toDoubleOrNull() ?: 0.0
}
