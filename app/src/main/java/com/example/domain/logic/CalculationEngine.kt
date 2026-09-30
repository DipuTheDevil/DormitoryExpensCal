package com.example.domain.logic

import com.example.domain.model.Member
import com.example.utils.BengaliFormatter
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

enum class BalanceType {
    CREDIT,  // ফেরত পাবেন
    DEBT,    // দিবেন
    SETTLED  // হিসাব সমতা
}

data class MemberBalance(
    val memberId: String,
    val memberName: String,
    val actualExpense: BigDecimal,
    val netBalance: BigDecimal,
    val type: BalanceType,
    val formattedAmount: String,
    val statusText: String
)

data class ExpenseSummary(
    val totalExpense: BigDecimal,
    val memberCount: Int,
    val perPersonExpense: BigDecimal,
    val formattedTotal: String,
    val formattedPerPerson: String,
    val memberBalances: List<MemberBalance>
)

class CalculationEngine {

    companion object {
        private val THRESHOLD = BigDecimal("0.01")
        private const val CURRENCY_SYMBOL = "৳ "
    }

    /**
     * Executes the equal sharing expense math for a list of members.
     * Computes total expense, per-person share, and individual debt/credit balances.
     */
    fun calculateExpenseSharing(members: List<Member>): ExpenseSummary {
        val count = members.size
        if (count == 0) {
            return ExpenseSummary(
                totalExpense = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP),
                memberCount = 0,
                perPersonExpense = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP),
                formattedTotal = formatCurrency(BigDecimal.ZERO),
                formattedPerPerson = formatCurrency(BigDecimal.ZERO),
                memberBalances = emptyList()
            )
        }

        // 1. Calculate total expense
        val totalExpense = members.fold(BigDecimal.ZERO) { sum, m ->
            sum.add(m.expense)
        }.setScale(2, RoundingMode.HALF_UP)

        // 2. Equal sharing calculation
        val countBd = BigDecimal.valueOf(count.toLong())
        val perPersonExpense = totalExpense.divide(countBd, 4, RoundingMode.HALF_UP)

        // 3. Calculate individual balances
        val balances = calculateMemberBalances(members, perPersonExpense)

        return ExpenseSummary(
            totalExpense = totalExpense,
            memberCount = count,
            perPersonExpense = perPersonExpense.setScale(2, RoundingMode.HALF_UP),
            formattedTotal = formatCurrency(totalExpense),
            formattedPerPerson = formatCurrency(perPersonExpense),
            memberBalances = balances
        )
    }

    /**
     * Calculates each member's debt or credit balance relative to the equal share.
     */
    fun calculateMemberBalances(
        members: List<Member>,
        perPersonShare: BigDecimal
    ): List<MemberBalance> {
        return members.mapIndexed { index, member ->
            val diff = member.expense.subtract(perPersonShare)

            val type = when {
                diff.compareTo(THRESHOLD) > 0 -> BalanceType.CREDIT
                diff.compareTo(THRESHOLD.negate()) < 0 -> BalanceType.DEBT
                else -> BalanceType.SETTLED
            }

            val absoluteAmount = when (type) {
                BalanceType.CREDIT -> diff.setScale(2, RoundingMode.HALF_UP)
                BalanceType.DEBT -> diff.abs().setScale(2, RoundingMode.HALF_UP)
                BalanceType.SETTLED -> BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
            }

            val serialNo = BengaliFormatter.toBengaliDigits(index + 1)
            val displayName = if (member.name.trim().isNotEmpty()) {
                member.name.trim()
            } else {
                "সদস্য $serialNo"
            }

            val formattedAmt = formatCurrency(absoluteAmount)
            val statusText = when (type) {
                BalanceType.CREDIT -> "ফেরত পাবেন $formattedAmt"
                BalanceType.DEBT -> "দিবেন $formattedAmt"
                BalanceType.SETTLED -> "হিসাব সমতা"
            }

            MemberBalance(
                memberId = member.id,
                memberName = displayName,
                actualExpense = member.expense.setScale(2, RoundingMode.HALF_UP),
                netBalance = diff.setScale(2, RoundingMode.HALF_UP),
                type = type,
                formattedAmount = formattedAmt,
                statusText = statusText
            )
        }
    }

    /**
     * Formats currency results to 2 decimal places using the '৳' symbol with Bengali numerals.
     * e.g., BigDecimal("1250.5") -> "৳ ১,২৫০.৫০"
     */
    fun formatCurrency(amount: BigDecimal): String {
        val scaled = amount.setScale(2, RoundingMode.HALF_UP)
        val df = DecimalFormat("#,##,##0.00", DecimalFormatSymbols(Locale.US))
        val englishFormatted = df.format(scaled)
        val bengaliFormatted = BengaliFormatter.toBengaliDigits(englishFormatted)
        return "$CURRENCY_SYMBOL$bengaliFormatted"
    }

    /**
     * Convenience overload for Double values.
     */
    fun formatCurrency(amount: Double): String {
        return formatCurrency(BigDecimal.valueOf(amount))
    }
}
