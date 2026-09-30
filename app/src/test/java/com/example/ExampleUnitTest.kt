package com.example

import com.example.data.model.SettlementStatus
import com.example.domain.ExpenseCalculator
import com.example.domain.logic.BalanceType
import com.example.domain.logic.CalculationEngine
import com.example.utils.BengaliFormatter
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class ExampleUnitTest {

    private val calculationEngine = CalculationEngine()

    @Test
    fun testBengaliNumeralsConversion() {
        assertEquals("০", BengaliFormatter.toBengaliDigits("0"))
        assertEquals("১", BengaliFormatter.toBengaliDigits("1"))
        assertEquals("১০", BengaliFormatter.toBengaliDigits("10"))
        assertEquals("১২৫০", BengaliFormatter.toBengaliDigits("1250"))
    }

    @Test
    fun testAsciiDigitsNormalization() {
        assertEquals("1250.50", BengaliFormatter.toAsciiDigits("১২৫০.৫০"))
        assertEquals("500", BengaliFormatter.toAsciiDigits("৫০০"))
    }

    @Test
    fun testMemberBengaliNumeralMapping() {
        val member = com.example.domain.model.Member(
            name = "রহিম",
            expense = BigDecimal("1250.50")
        )
        assertEquals("১২৫০.৫০", member.expenseInBengaliDigits)
        assertEquals("১,২৫০.৫০", member.formattedExpense)
        assertEquals("৳ ১,২৫০.৫০", member.formattedExpenseWithCurrency)
        assertEquals("রহিম", member.getDisplayName(0))

        val emptyNameMember = com.example.domain.model.Member(
            name = "",
            expense = BigDecimal.ZERO
        )
        assertEquals("সদস্য ১", emptyNameMember.getDisplayName(0))
        assertEquals("সদস্য ২", emptyNameMember.getDisplayName(1))
    }

    @Test
    fun testCalculationEngineFormatting() {
        assertEquals("৳ ১,২৫০.৫০", calculationEngine.formatCurrency(BigDecimal("1250.50")))
        assertEquals("৳ ২৫০.০০", calculationEngine.formatCurrency(BigDecimal("250")))
        assertEquals("৳ ০.০০", calculationEngine.formatCurrency(BigDecimal.ZERO))
        assertEquals("৳ ৫০০.৭৫", calculationEngine.formatCurrency(500.75))
    }

    @Test
    fun testCalculationEngineExpenseSharing() {
        val members = listOf(
            com.example.domain.model.Member(name = "রহিম", expense = BigDecimal("600.00")),
            com.example.domain.model.Member(name = "করিম", expense = BigDecimal("300.00")),
            com.example.domain.model.Member(name = "সুমন", expense = BigDecimal("0.00"))
        )

        val summary = calculationEngine.calculateExpenseSharing(members)

        // Total: 900.00, Members: 3, Per person: 300.00
        assertEquals(BigDecimal("900.00"), summary.totalExpense)
        assertEquals(3, summary.memberCount)
        assertEquals(BigDecimal("300.00"), summary.perPersonExpense)
        assertEquals("৳ ৯০০.০০", summary.formattedTotal)
        assertEquals("৳ ৩০০.০০", summary.formattedPerPerson)

        assertEquals(3, summary.memberBalances.size)

        // Rahim: 600 - 300 = +300 (CREDIT)
        val b0 = summary.memberBalances[0]
        assertEquals("রহিম", b0.memberName)
        assertEquals(BalanceType.CREDIT, b0.type)
        assertEquals(BigDecimal("300.00"), b0.netBalance)
        assertEquals("৳ ৩০০.০০", b0.formattedAmount)
        assertEquals("ফেরত পাবেন ৳ ৩০০.০০", b0.statusText)

        // Karim: 300 - 300 = 0 (SETTLED)
        val b1 = summary.memberBalances[1]
        assertEquals("করিম", b1.memberName)
        assertEquals(BalanceType.SETTLED, b1.type)
        assertEquals(BigDecimal("0.00"), b1.netBalance)
        assertEquals("হিসাব সমতা", b1.statusText)

        // Sumon: 0 - 300 = -300 (DEBT)
        val b2 = summary.memberBalances[2]
        assertEquals("সুমন", b2.memberName)
        assertEquals(BalanceType.DEBT, b2.type)
        assertEquals(BigDecimal("-300.00"), b2.netBalance)
        assertEquals("৳ ৩০০.০০", b2.formattedAmount)
        assertEquals("দিবেন ৳ ৩০০.০০", b2.statusText)
    }

    @Test
    fun testExpenseCalculationLogic() {
        val members = listOf(
            com.example.data.model.Member(name = "রহিম", expense = "500"),
            com.example.data.model.Member(name = "করিম", expense = "250"),
            com.example.data.model.Member(name = "সুমন", expense = "0")
        )

        val result = ExpenseCalculator.calculate(members)

        assertEquals(750.0, result.totalExpense, 0.001)
        assertEquals(3, result.totalMembers)
        assertEquals(250.0, result.perPersonExpense, 0.001)

        val settlements = result.settlements
        assertEquals(3, settlements.size)

        // Rahim: 500 - 250 = +250 (GET)
        assertEquals("রহিম", settlements[0].displayName)
        assertEquals(SettlementStatus.GET, settlements[0].status)
        assertEquals(250.0, settlements[0].amount, 0.001)

        // Karim: 250 - 250 = 0 (EVEN)
        assertEquals("করিম", settlements[1].displayName)
        assertEquals(SettlementStatus.EVEN, settlements[1].status)
        assertEquals(0.0, settlements[1].amount, 0.001)

        // Sumon: 0 - 250 = -250 (GIVE)
        assertEquals("সুমন", settlements[2].displayName)
        assertEquals(SettlementStatus.GIVE, settlements[2].status)
        assertEquals(250.0, settlements[2].amount, 0.001)
    }

    @Test
    fun testDefaultMemberDisplayNameWhenNameEmpty() {
        val members = listOf(
            com.example.data.model.Member(name = "", expense = "100"),
            com.example.data.model.Member(name = "  ", expense = "100")
        )

        val result = ExpenseCalculator.calculate(members)
        assertEquals("সদস্য ১", result.settlements[0].displayName)
        assertEquals("সদস্য ২", result.settlements[1].displayName)
    }

    @Test
    fun testShareReportHelperTextGeneration() {
        val members = listOf(
            com.example.data.model.Member(name = "রহিম", expense = "500"),
            com.example.data.model.Member(name = "করিম", expense = "250"),
            com.example.data.model.Member(name = "সুমন", expense = "0")
        )
        val calc = ExpenseCalculator.calculate(members)
        val shareText = com.example.utils.ShareReportHelper.generateSummaryText(members, calc)

        org.junit.Assert.assertTrue(shareText.contains("মোট সদস্য:* ৩ জন"))
        org.junit.Assert.assertTrue(shareText.contains("মোট খরচ:* ৳ ৭৫০.০০"))
        org.junit.Assert.assertTrue(shareText.contains("মাথাপিছু সমান ভাগ:* ৳ ২৫০.০০"))
        org.junit.Assert.assertTrue(shareText.contains("১. রহিম: ৳ ৫০০.০০"))
        org.junit.Assert.assertTrue(shareText.contains("• *রহিম*: ফেরত পাবেন ৳ ২৫০.০০"))
        org.junit.Assert.assertTrue(shareText.contains("• *করিম*: হিসাব সমতা"))
        org.junit.Assert.assertTrue(shareText.contains("• *সুমন*: দিবেন ৳ ২৫০.০০"))
    }

    @Test
    fun testCsvExportHelperContentGeneration() {
        val members = listOf(
            com.example.data.model.Member(name = "রহিম", expense = "500"),
            com.example.data.model.Member(name = "করিম", expense = "250"),
            com.example.data.model.Member(name = "সুমন", expense = "0")
        )
        val calc = ExpenseCalculator.calculate(members)
        val csv = com.example.utils.CsvExportHelper.generateCsvContent(members, calc)

        // Must start with UTF-8 BOM for Excel compatibility
        org.junit.Assert.assertTrue(csv.startsWith("\uFEFF"))
        org.junit.Assert.assertTrue(csv.contains("হিসাব Pro - মেসের বাজার খরচ রিপোর্ট"))
        org.junit.Assert.assertTrue(csv.contains("মোট সদস্য সংখ্যা"))
        org.junit.Assert.assertTrue(csv.contains("750.00"))
        org.junit.Assert.assertTrue(csv.contains("250.00"))
        org.junit.Assert.assertTrue(csv.contains("রহিম"))
        org.junit.Assert.assertTrue(csv.contains("করিম"))
        org.junit.Assert.assertTrue(csv.contains("সুমন"))
        org.junit.Assert.assertTrue(csv.contains("ফেরত পাবেন"))
        org.junit.Assert.assertTrue(csv.contains("হিসাব সমতা"))
        org.junit.Assert.assertTrue(csv.contains("দিবেন"))
        org.junit.Assert.assertTrue(csv.contains("সর্বমোট"))
    }
}
