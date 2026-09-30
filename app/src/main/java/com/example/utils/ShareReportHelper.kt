package com.example.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.data.model.CalculationResult
import com.example.data.model.Member
import com.example.data.model.SettlementStatus
import com.example.domain.ExpenseCalculator
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Utility helper to generate clean, text-based summaries of mess expenses
 * and share them via WhatsApp, Messenger, SMS, and other messaging apps
 * using Bengali numerals and currency formatting.
 */
object ShareReportHelper {

    /**
     * Generates a beautifully formatted text report with Bengali numerals
     * and markdown bolding for messaging apps like WhatsApp.
     */
    fun generateSummaryText(
        members: List<Member>,
        calculation: CalculationResult
    ): String {
        val calc = if (calculation.hasCalculated) calculation else ExpenseCalculator.calculate(members)

        val totalMembersStr = BengaliFormatter.toBengaliDigits(calc.totalMembers)
        val totalExpenseStr = formatMoney(calc.totalExpense)
        val perPersonStr = formatMoney(calc.perPersonExpense)

        val timestamp = SimpleDateFormat("dd/MM/yyyy, hh:mm a", Locale.US).format(Date())
        val bengaliTimestamp = BengaliFormatter.toBengaliDigits(timestamp)

        val sb = StringBuilder()
        sb.append("📊 *বাজার খরচ হিসাব বিবরণী (হিসাব Pro)*\n")
        sb.append("📅 তারিখ: $bengaliTimestamp\n")
        sb.append("━━━━━━━━━━━━━━━━━━━━\n")
        sb.append("👥 *মোট সদস্য:* $totalMembersStr জন\n")
        sb.append("💰 *মোট খরচ:* ৳ $totalExpenseStr\n")
        sb.append("⚖️ *মাথাপিছু সমান ভাগ:* ৳ $perPersonStr\n\n")

        // Individual member expenses
        sb.append("📋 *সদস্যদের ব্যক্তিগত খরচ:*\n")
        members.forEachIndexed { index, member ->
            val serial = BengaliFormatter.toBengaliDigits(index + 1)
            val name = if (member.name.trim().isNotEmpty()) member.name.trim() else "সদস্য $serial"
            val exp = formatMoney(member.numericExpense)
            sb.append("$serial. $name: ৳ $exp\n")
        }

        sb.append("\n⚖️ *দেনা-পাওনার ফাইনাল হিসাব:*\n")
        if (calc.settlements.isEmpty()) {
            sb.append("• কোনো হিসাব পাওয়া যায়নি।\n")
        } else {
            calc.settlements.forEach { item ->
                when (item.status) {
                    SettlementStatus.GET -> {
                        val amt = formatMoney(item.amount)
                        sb.append("• *${item.displayName}*: ফেরত পাবেন ৳ $amt 🟢\n")
                    }
                    SettlementStatus.GIVE -> {
                        val amt = formatMoney(item.amount)
                        sb.append("• *${item.displayName}*: দিবেন ৳ $amt 🔴\n")
                    }
                    SettlementStatus.EVEN -> {
                        sb.append("• *${item.displayName}*: হিসাব সমতা ⚪\n")
                    }
                }
            }
        }

        sb.append("━━━━━━━━━━━━━━━━━━━━\n")
        sb.append("📲 হিসাব Pro - মেসের খরচ ভাগ করার স্মার্ট ক্যালকুলেটর")

        return sb.toString()
    }

    /**
     * Shares the text-based settlement summary via Android's share chooser
     * allowing users to send to WhatsApp, Telegram, Messenger, SMS, etc.
     */
    fun shareSettlementSummary(
        context: Context,
        members: List<Member>,
        calculation: CalculationResult
    ) {
        val summaryText = generateSummaryText(members, calculation)
        shareText(context, summaryText)
    }

    /**
     * General text sharing via standard Android Chooser.
     */
    fun shareText(context: Context, text: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            putExtra(Intent.EXTRA_SUBJECT, "বাজার খরচ হিসাব - হিসাব Pro")
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, "বাজার খরচ হিসাব বিবরণী শেয়ার করুন")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        try {
            context.startActivity(shareIntent)
        } catch (_: Exception) {
            sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(sendIntent)
        }
    }

    /**
     * Attempts direct WhatsApp share, falling back to standard chooser if not installed.
     */
    fun shareDirectWhatsApp(context: Context, text: String) {
        val whatsappIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            setPackage("com.whatsapp")
            putExtra(Intent.EXTRA_TEXT, text)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            context.startActivity(whatsappIntent)
        } catch (_: Exception) {
            // WhatsApp not installed, open standard chooser
            shareText(context, text)
        }
    }

    /**
     * Copies the formatted settlement summary text to device clipboard.
     */
    fun copyToClipboard(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clip = ClipData.newPlainText("হিসাব Pro বাজার খরচ", text)
        clipboard?.setPrimaryClip(clip)
        Toast.makeText(context, "হিসাব বিবরণী ক্লিপবোর্ডে কপি করা হয়েছে!", Toast.LENGTH_SHORT).show()
    }

    private fun formatMoney(amount: Double): String {
        val df = DecimalFormat("#,##,##0.00", DecimalFormatSymbols(Locale.US))
        return BengaliFormatter.toBengaliDigits(df.format(amount))
    }
}
