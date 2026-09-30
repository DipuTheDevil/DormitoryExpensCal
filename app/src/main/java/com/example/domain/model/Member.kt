package com.example.domain.model

import com.example.utils.BengaliFormatter
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import java.util.UUID

/**
 * Domain model representing a Mess member.
 * Uses [BigDecimal] for exact monetary calculations and provides
 * mapping functions/properties to Bengali numerals for UI display.
 */
data class Member(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val expense: BigDecimal = BigDecimal.ZERO
) {
    constructor(id: String = UUID.randomUUID().toString(), name: String, expenseDouble: Double) : this(
        id = id,
        name = name,
        expense = BigDecimal.valueOf(expenseDouble)
    )

    constructor(id: String = UUID.randomUUID().toString(), name: String, expenseString: String) : this(
        id = id,
        name = name,
        expense = BengaliFormatter.toAsciiDigits(expenseString).trim().toBigDecimalOrNull() ?: BigDecimal.ZERO
    )

    /**
     * Expense value mapped directly to Bengali digits without currency symbol or commas.
     * e.g., BigDecimal("1250.50") -> "১২৫০.৫০"
     */
    val expenseInBengaliDigits: String
        get() = BengaliFormatter.toBengaliDigits(expense.setScale(2, RoundingMode.HALF_UP).toPlainString())

    /**
     * Formatted expense string with Indian-numbering commas and Bengali digits.
     * e.g., BigDecimal("1250.50") -> "১,২৫০.৫০"
     */
    val formattedExpense: String
        get() {
            val scaled = expense.setScale(2, RoundingMode.HALF_UP)
            val df = DecimalFormat("#,##,##0.00", DecimalFormatSymbols(Locale.US))
            return BengaliFormatter.toBengaliDigits(df.format(scaled))
        }

    /**
     * Formatted expense string prefixed with the Taka currency symbol '৳'.
     * e.g., BigDecimal("1250.50") -> "৳ ১,২৫০.৫০"
     */
    val formattedExpenseWithCurrency: String
        get() = "৳ $formattedExpense"

    /**
     * Returns the member's name if non-empty, otherwise falls back to
     * default localized display name (e.g. "সদস্য ১", "সদস্য ২").
     */
    fun getDisplayName(index: Int): String {
        return if (name.trim().isNotEmpty()) {
            name.trim()
        } else {
            val serialNo = BengaliFormatter.toBengaliDigits(index + 1)
            "সদস্য $serialNo"
        }
    }
}
