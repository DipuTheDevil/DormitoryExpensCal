package com.example.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object BengaliFormatter {
    private val bengaliDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')

    fun toBengaliDigits(input: String): String {
        val sb = StringBuilder(input.length)
        for (ch in input) {
            if (ch in '0'..'9') {
                sb.append(bengaliDigits[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    fun toBengaliDigits(num: Number): String {
        return toBengaliDigits(num.toString())
    }

    /**
     * Converts Bengali digits to standard ASCII digits 0-9
     * so user can type using either English or Bengali keyboard.
     */
    fun toAsciiDigits(input: String): String {
        val sb = StringBuilder(input.length)
        for (ch in input) {
            when (ch) {
                '০' -> sb.append('0')
                '১' -> sb.append('1')
                '২' -> sb.append('2')
                '৩' -> sb.append('3')
                '৪' -> sb.append('4')
                '৫' -> sb.append('5')
                '৬' -> sb.append('6')
                '৭' -> sb.append('7')
                '৮' -> sb.append('8')
                '৯' -> sb.append('9')
                '।' -> sb.append('.')
                else -> sb.append(ch)
            }
        }
        return sb.toString()
    }

    /**
     * Formats currency with commas and up to 2 decimals, in Bengali numerals.
     * e.g., 1250.0 -> "১,২৫০", 1250.5 -> "১,২৫০.৫০"
     */
    fun formatCurrency(amount: Double): String {
        val df = DecimalFormat("#,##,##0.##", DecimalFormatSymbols(Locale.US)).apply {
            minimumFractionDigits = if (amount % 1.0 != 0.0) 2 else 0
            maximumFractionDigits = 2
        }
        val formatted = df.format(amount)
        return toBengaliDigits(formatted)
    }

    /**
     * Formats exact 2 decimal places for settlement differences (like toFixed(2))
     */
    fun formatFixedTwo(amount: Double): String {
        val df = DecimalFormat("0.00", DecimalFormatSymbols(Locale.US))
        val formatted = df.format(amount)
        return toBengaliDigits(formatted)
    }
}
