package com.example.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.data.model.CalculationResult
import com.example.data.model.Member
import com.example.data.model.SettlementStatus
import com.example.domain.ExpenseCalculator
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Utility helper to generate standard RFC-4180 CSV export files from mess expense records.
 * Optimized for spreadsheet applications like Microsoft Excel, Google Sheets, and LibreOffice.
 */
object CsvExportHelper {

    private val numberFormat = DecimalFormat("0.00", DecimalFormatSymbols(Locale.US))

    /**
     * Generates a clean CSV formatted string with UTF-8 Byte Order Mark (BOM)
     * so that Unicode Bengali characters render perfectly without gibberish in Excel and Sheets.
     */
    fun generateCsvContent(
        members: List<Member>,
        calculation: CalculationResult
    ): String {
        val calc = if (calculation.hasCalculated) calculation else ExpenseCalculator.calculate(members)
        val sb = StringBuilder()

        // 1. UTF-8 Byte Order Mark (BOM) for Excel Unicode compatibility
        sb.append("\uFEFF")

        // 2. Report Metadata Header
        val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.US).format(Date())
        sb.append(csvRow("হিসাব Pro - মেসের বাজার খরচ রিপোর্ট", ""))
        sb.append(csvRow("রিপোর্ট তৈরির তারিখ", dateFormat))
        sb.append(csvRow("মোট সদস্য সংখ্যা", calc.totalMembers.toString()))
        sb.append(csvRow("মোট বাজার খরচ (টাকা)", numberFormat.format(calc.totalExpense)))
        sb.append(csvRow("মাথাপিছু সমান ভাগ (টাকা)", numberFormat.format(calc.perPersonExpense)))
        sb.append("\n")

        // 3. Table Column Headers
        sb.append(
            csvRow(
                "ক্রমিক",
                "সদস্যের নাম",
                "ব্যক্তিগত খরচ (টাকা)",
                "মাথাপিছু সমান ভাগ (টাকা)",
                "ব্যালেন্স / পার্থক্য (টাকা)",
                "অবস্থা",
                "দেনা-পাওনার বিবরণ"
            )
        )

        // 4. Data Rows
        val settlementsMap = calc.settlements.associateBy { it.memberId }

        members.forEachIndexed { index, member ->
            val serial = (index + 1).toString()
            val name = if (member.name.trim().isNotEmpty()) member.name.trim() else "সদস্য $serial"
            val exp = member.numericExpense
            val balance = exp - calc.perPersonExpense
            val item = settlementsMap[member.id]

            val statusText = when (item?.status) {
                SettlementStatus.GET -> "ফেরত পাবেন"
                SettlementStatus.GIVE -> "দিবেন"
                SettlementStatus.EVEN -> "হিসাব সমতা"
                null -> if (balance > 0.001) "ফেরত পাবেন" else if (balance < -0.001) "দিবেন" else "হিসাব সমতা"
            }

            val settlementDesc = when (item?.status) {
                SettlementStatus.GET -> "ফেরত পাবেন ৳ ${numberFormat.format(item.amount)}"
                SettlementStatus.GIVE -> "দিবেন ৳ ${numberFormat.format(item.amount)}"
                SettlementStatus.EVEN -> "হিসাব সমতা"
                null -> if (balance > 0.001) {
                    "ফেরত পাবেন ৳ ${numberFormat.format(balance)}"
                } else if (balance < -0.001) {
                    "দিবেন ৳ ${numberFormat.format(-balance)}"
                } else {
                    "হিসাব সমতা"
                }
            }

            sb.append(
                csvRow(
                    serial,
                    name,
                    numberFormat.format(exp),
                    numberFormat.format(calc.perPersonExpense),
                    numberFormat.format(balance),
                    statusText,
                    settlementDesc
                )
            )
        }

        // 5. Total Row
        sb.append("\n")
        sb.append(
            csvRow(
                "সর্বমোট",
                "",
                numberFormat.format(calc.totalExpense),
                "",
                "0.00",
                "",
                ""
            )
        )

        return sb.toString()
    }

    /**
     * Writes CSV data to cache and triggers Android system share chooser
     * to share or save the file to Google Drive, WhatsApp, Sheets, Files, etc.
     */
    fun exportAndShareCsv(
        context: Context,
        members: List<Member>,
        calculation: CalculationResult
    ): File? {
        return try {
            val content = generateCsvContent(members, calculation)
            val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val file = File(exportDir, "HisabPro_Bazar_Khoroch_$timeStamp.csv")

            FileOutputStream(file).use { out ->
                out.write(content.toByteArray(Charsets.UTF_8))
            }

            val authority = "${context.packageName}.fileprovider"
            val uri = FileProvider.getUriForFile(context, authority, file)

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "বাজার খরচ হিসাব - CSV ফাইল")
                putExtra(Intent.EXTRA_TEXT, "হিসাব Pro মেসের বাজার খরচ এক্সেল / স্প্রেডশিট ফাইল।")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val chooser = Intent.createChooser(shareIntent, "CSV ফাইল ডাউনলোড বা শেয়ার করুন (Excel / Sheets)").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
            file
        } catch (_: Exception) {
            null
        }
    }

    private fun csvRow(vararg columns: String): String {
        return columns.joinToString(",") { escapeCsv(it) } + "\r\n"
    }

    private fun escapeCsv(value: String): String {
        val containsSpecial = value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")
        return if (containsSpecial) {
            "\"" + value.replace("\"", "\"\"") + "\""
        } else {
            "\"$value\""
        }
    }
}
