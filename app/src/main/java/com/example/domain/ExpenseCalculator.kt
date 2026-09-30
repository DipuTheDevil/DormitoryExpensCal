package com.example.domain

import com.example.data.model.CalculationResult
import com.example.data.model.SettlementItem
import com.example.data.model.SettlementStatus
import com.example.utils.BengaliFormatter
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.abs

object ExpenseCalculator {

    fun calculate(members: List<com.example.data.model.Member>): CalculationResult {
        val domainMembers = members.map { member ->
            val asciiStr = BengaliFormatter.toAsciiDigits(member.expense).trim()
            val bdExpense = asciiStr.toBigDecimalOrNull() ?: BigDecimal.ZERO
            com.example.domain.model.Member(
                id = member.id,
                name = member.name,
                expense = bdExpense
            )
        }
        return calculateDomain(domainMembers)
    }

    fun calculateDomain(members: List<com.example.domain.model.Member>): CalculationResult {
        val count = members.size
        if (count == 0) {
            return CalculationResult(hasCalculated = true)
        }

        val totalExpense = members.fold(BigDecimal.ZERO) { sum, member ->
            sum.add(member.expense)
        }

        val countBd = BigDecimal.valueOf(count.toLong())
        val perPerson = if (count > 0) {
            totalExpense.divide(countBd, 4, RoundingMode.HALF_UP)
        } else {
            BigDecimal.ZERO
        }

        val settlements = members.mapIndexed { index, member ->
            val diff = member.expense.subtract(perPerson)
            val threshold = BigDecimal("0.01")

            val status = when {
                diff.compareTo(threshold) > 0 -> SettlementStatus.GET
                diff.compareTo(threshold.negate()) < 0 -> SettlementStatus.GIVE
                else -> SettlementStatus.EVEN
            }

            val amount = when (status) {
                SettlementStatus.GET -> diff.setScale(2, RoundingMode.HALF_UP).toDouble()
                SettlementStatus.GIVE -> diff.abs().setScale(2, RoundingMode.HALF_UP).toDouble()
                SettlementStatus.EVEN -> 0.0
            }

            val serialNo = BengaliFormatter.toBengaliDigits(index + 1)
            val displayName = if (member.name.trim().isNotEmpty()) {
                member.name.trim()
            } else {
                "সদস্য $serialNo"
            }

            SettlementItem(
                memberId = member.id,
                displayName = displayName,
                status = status,
                amount = amount
            )
        }

        return CalculationResult(
            totalExpense = totalExpense.setScale(2, RoundingMode.HALF_UP).toDouble(),
            totalMembers = count,
            perPersonExpense = perPerson.setScale(2, RoundingMode.HALF_UP).toDouble(),
            settlements = settlements,
            hasCalculated = true
        )
    }
}
