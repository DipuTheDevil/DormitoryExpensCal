package com.example.utils

import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import androidx.core.content.res.ResourcesCompat
import com.example.R
import com.example.data.model.CalculationResult
import com.example.data.model.Member
import com.example.data.model.SettlementStatus
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Helper class to generate and print PDF reports matching the 'হিসাব Pro'
 * Dark Navy Glassmorphism design language using Android's [PrintManager]
 * and native [PdfDocument] APIs.
 */
object PrintReportHelper {

    private const val PAGE_WIDTH = 595 // A4 standard width (points @ 72 dpi)
    private const val PAGE_HEIGHT = 842 // A4 standard height (points @ 72 dpi)

    /**
     * Initiates printing or PDF saving via Android [PrintManager].
     */
    fun printReport(
        context: Context,
        members: List<Member>,
        calculation: CalculationResult
    ) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
            ?: return

        val jobName = "হিসাব_Pro_বাজার_খরচ_${System.currentTimeMillis()}"
        val adapter = PdfReportPrintAdapter(context, members, calculation)
        val printAttributes = PrintAttributes.Builder()
            .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
            .setColorMode(PrintAttributes.COLOR_MODE_COLOR)
            .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
            .build()

        printManager.print(jobName, adapter, printAttributes)
    }

    /**
     * Generates a native [PdfDocument] capturing the complete settlement summary
     * with the dark navy glassmorphism layout.
     */
    fun generatePdfDocument(
        context: Context,
        members: List<Member>,
        calculation: CalculationResult
    ): PdfDocument {
        val pdfDocument = PdfDocument()

        val fontNormal = try {
            ResourcesCompat.getFont(context, R.font.hind_siliguri) ?: Typeface.SANS_SERIF
        } catch (_: Exception) {
            Typeface.SANS_SERIF
        }
        val fontBold = Typeface.create(fontNormal, Typeface.BOLD)

        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        // 1. Overall Canvas Background (Deep Dark Navy #0F172A)
        val bgPaint = Paint().apply {
            color = Color.parseColor("#0F172A")
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, PAGE_WIDTH.toFloat(), PAGE_HEIGHT.toFloat(), bgPaint)

        // 2. Outer Glassmorphism Card Container (#1E293B)
        val outerMargin = 22f
        val outerCardRect = RectF(
            outerMargin,
            outerMargin,
            PAGE_WIDTH - outerMargin,
            PAGE_HEIGHT - outerMargin
        )

        val cardBgPaint = Paint().apply {
            color = Color.parseColor("#1E293B")
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        val cardBorderPaint = Paint().apply {
            color = Color.parseColor("#334155")
            style = Paint.Style.STROKE
            strokeWidth = 1.2f
            isAntiAlias = true
        }
        canvas.drawRoundRect(outerCardRect, 16f, 16f, cardBgPaint)
        canvas.drawRoundRect(outerCardRect, 16f, 16f, cardBorderPaint)

        val contentLeft = outerMargin + 16f
        val contentRight = PAGE_WIDTH - outerMargin - 16f
        val contentWidth = contentRight - contentLeft

        // 3. Header Hero Box (Indigo to Purple Gradient)
        val headerTop = outerMargin + 16f
        val headerBottom = headerTop + 64f
        val headerRect = RectF(contentLeft, headerTop, contentRight, headerBottom)

        val headerGradient = LinearGradient(
            contentLeft, headerTop, contentRight, headerBottom,
            Color.parseColor("#4F46E5"), Color.parseColor("#7C3AED"),
            Shader.TileMode.CLAMP
        )
        val headerPaint = Paint().apply {
            shader = headerGradient
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawRoundRect(headerRect, 12f, 12f, headerPaint)

        // Header Title
        val headerTitlePaint = Paint().apply {
            color = Color.WHITE
            typeface = fontBold
            textSize = 17f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText(
            "বাজার খরচ হিসাব ক্যালকুলেটর",
            headerRect.centerX(),
            headerTop + 28f,
            headerTitlePaint
        )

        // Header Subtitle
        val headerSubPaint = Paint().apply {
            color = Color.parseColor("#E0E7FF")
            typeface = fontNormal
            textSize = 9.5f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText(
            "সহজেই মেসের বাজার খরচ সবার মধ্যে সমানভাবে ভাগ করুন",
            headerRect.centerX(),
            headerTop + 48f,
            headerSubPaint
        )

        var yCursor = headerBottom + 16f

        // 4. Section: সদস্যদের তালিকা ও খরচ (Members List)
        val sectionTitlePaint = Paint().apply {
            color = Color.parseColor("#E2E8F0")
            typeface = fontBold
            textSize = 11.5f
            isAntiAlias = true
        }
        canvas.drawText("সদস্যদের তালিকা ও খরচ", contentLeft, yCursor, sectionTitlePaint)
        yCursor += 10f

        val memberRowBgPaint = Paint().apply {
            color = Color.parseColor("#0F172A")
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        val memberRowBorderPaint = Paint().apply {
            color = Color.parseColor("#334155")
            style = Paint.Style.STROKE
            strokeWidth = 0.8f
            isAntiAlias = true
        }

        val namePaint = Paint().apply {
            color = Color.parseColor("#38BDF8") // Sky blue
            typeface = fontBold
            textSize = 9.5f
            isAntiAlias = true
        }
        val expensePaint = Paint().apply {
            color = Color.parseColor("#818CF8") // Indigo
            typeface = fontBold
            textSize = 9.5f
            textAlign = Paint.Align.RIGHT
            isAntiAlias = true
        }
        val labelMutedPaint = Paint().apply {
            color = Color.parseColor("#94A3B8")
            typeface = fontNormal
            textSize = 8.5f
            isAntiAlias = true
        }

        val rowHeight = 24f
        val rowSpacing = 5f

        // Limit display count on single page to prevent overlap if list is huge
        val displayMembers = members.take(10)
        displayMembers.forEachIndexed { index, m ->
            val rowRect = RectF(contentLeft, yCursor, contentRight, yCursor + rowHeight)
            canvas.drawRoundRect(rowRect, 6f, 6f, memberRowBgPaint)
            canvas.drawRoundRect(rowRect, 6f, 6f, memberRowBorderPaint)

            val serial = BengaliFormatter.toBengaliDigits(index + 1)
            val nameDisplay = if (m.name.trim().isNotEmpty()) m.name.trim() else "সদস্য $serial"
            val expDisplay = BengaliFormatter.formatCurrency(m.numericExpense)

            canvas.drawText("সদস্য $serial :  $nameDisplay", contentLeft + 10f, yCursor + 16f, namePaint)
            canvas.drawText("মোট খরচ: ৳ $expDisplay", contentRight - 10f, yCursor + 16f, expensePaint)

            yCursor += rowHeight + rowSpacing
        }

        if (members.size > 10) {
            val moreCount = BengaliFormatter.toBengaliDigits(members.size - 10)
            canvas.drawText("... আরও $moreCount জন সদস্য অন্তর্ভুক্ত রয়েছে", contentLeft + 10f, yCursor + 12f, labelMutedPaint)
            yCursor += 16f
        }

        yCursor += 8f

        // 5. Section: Summary Dashboard (৩টি মেট্রিক কার্ড)
        val cardWidth = (contentWidth - 16f) / 3f
        val summaryCardHeight = 48f

        val totalExpStr = BengaliFormatter.formatCurrency(calculation.totalExpense)
        val membersCountStr = BengaliFormatter.toBengaliDigits(calculation.totalMembers)
        val perPersonStr = BengaliFormatter.formatCurrency(calculation.perPersonExpense)

        // Card 1: মোট খরচ
        val c1Rect = RectF(contentLeft, yCursor, contentLeft + cardWidth, yCursor + summaryCardHeight)
        canvas.drawRoundRect(c1Rect, 8f, 8f, memberRowBgPaint)
        canvas.drawRoundRect(c1Rect, 8f, 8f, memberRowBorderPaint)
        val cardTitlePaint = Paint().apply {
            color = Color.parseColor("#94A3B8")
            typeface = fontNormal
            textSize = 8.5f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("মোট খরচ", c1Rect.centerX(), yCursor + 18f, cardTitlePaint)
        val c1ValPaint = Paint().apply {
            color = Color.parseColor("#38BDF8")
            typeface = fontBold
            textSize = 12.5f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("৳ $totalExpStr", c1Rect.centerX(), yCursor + 36f, c1ValPaint)

        // Card 2: মোট সদস্য
        val c2Left = contentLeft + cardWidth + 8f
        val c2Rect = RectF(c2Left, yCursor, c2Left + cardWidth, yCursor + summaryCardHeight)
        canvas.drawRoundRect(c2Rect, 8f, 8f, memberRowBgPaint)
        canvas.drawRoundRect(c2Rect, 8f, 8f, memberRowBorderPaint)
        canvas.drawText("মোট সদস্য", c2Rect.centerX(), yCursor + 18f, cardTitlePaint)
        val c2ValPaint = Paint().apply {
            color = Color.parseColor("#F8FAFC")
            typeface = fontBold
            textSize = 12.5f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText(membersCountStr, c2Rect.centerX(), yCursor + 36f, c2ValPaint)

        // Card 3: মাথাপিছু খরচ (সমান ভাগ)
        val c3Left = c2Left + cardWidth + 8f
        val c3Rect = RectF(c3Left, yCursor, c3Left + cardWidth, yCursor + summaryCardHeight)
        canvas.drawRoundRect(c3Rect, 8f, 8f, memberRowBgPaint)
        canvas.drawRoundRect(c3Rect, 8f, 8f, memberRowBorderPaint)
        canvas.drawText("মাথাপিছু খরচ", c3Rect.centerX(), yCursor + 18f, cardTitlePaint)
        val c3ValPaint = Paint().apply {
            color = Color.parseColor("#10B981") // Success green
            typeface = fontBold
            textSize = 12.5f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("৳ $perPersonStr", c3Rect.centerX(), yCursor + 36f, c3ValPaint)

        yCursor += summaryCardHeight + 16f

        // 6. Section: দেনা-পাওনার ফাইনাল হিসাব (Settlement Summary)
        val settlementTitlePaint = Paint().apply {
            color = Color.parseColor("#A7F3D0")
            typeface = fontBold
            textSize = 11.5f
            isAntiAlias = true
        }
        canvas.drawText("দেনা-পাওনার ফাইনাল হিসাব", contentLeft, yCursor, settlementTitlePaint)
        yCursor += 10f

        val displaySettlements = calculation.settlements.take(12)
        if (displaySettlements.isEmpty()) {
            val emptyPaint = Paint().apply {
                color = Color.parseColor("#94A3B8")
                typeface = fontNormal
                textSize = 9.5f
                isAntiAlias = true
            }
            canvas.drawText("কোনো হিসাব পাওয়া যায়নি।", contentLeft + 10f, yCursor + 16f, emptyPaint)
            yCursor += 26f
        } else {
            val settleRowHeight = 26f
            val settleSpacing = 5f

            displaySettlements.forEach { item ->
                val sRect = RectF(contentLeft, yCursor, contentRight, yCursor + settleRowHeight)
                canvas.drawRoundRect(sRect, 6f, 6f, memberRowBgPaint)
                canvas.drawRoundRect(sRect, 6f, 6f, memberRowBorderPaint)

                // Member Name
                val settleNamePaint = Paint().apply {
                    color = Color.parseColor("#F8FAFC")
                    typeface = fontBold
                    textSize = 9.5f
                    isAntiAlias = true
                }
                canvas.drawText(item.displayName, contentLeft + 10f, yCursor + 17f, settleNamePaint)

                // Badge / Pill
                val badgeText: String
                val badgeBgColor: Int
                val badgeBorderColor: Int
                val badgeTextColor: Int

                when (item.status) {
                    SettlementStatus.GET -> {
                        badgeText = "ফেরত পাবেন ৳ ${BengaliFormatter.formatFixedTwo(item.amount)}"
                        badgeBgColor = Color.parseColor("#064E3B")
                        badgeBorderColor = Color.parseColor("#10B981")
                        badgeTextColor = Color.parseColor("#6EE7B7")
                    }
                    SettlementStatus.GIVE -> {
                        badgeText = "দিবেন ৳ ${BengaliFormatter.formatFixedTwo(item.amount)}"
                        badgeBgColor = Color.parseColor("#7F1D1D")
                        badgeBorderColor = Color.parseColor("#EF4444")
                        badgeTextColor = Color.parseColor("#FCA5A5")
                    }
                    SettlementStatus.EVEN -> {
                        badgeText = "হিসাব সমতা"
                        badgeBgColor = Color.parseColor("#1E293B")
                        badgeBorderColor = Color.parseColor("#64748B")
                        badgeTextColor = Color.parseColor("#CBD5E1")
                    }
                }

                val pillPaint = Paint().apply {
                    typeface = fontBold
                    textSize = 8.5f
                    isAntiAlias = true
                }
                val textWidth = pillPaint.measureText(badgeText)
                val pillWidth = textWidth + 16f
                val pillHeight = 18f
                val pillRect = RectF(
                    contentRight - pillWidth - 8f,
                    yCursor + 4f,
                    contentRight - 8f,
                    yCursor + 4f + pillHeight
                )

                val pillBgPaint = Paint().apply {
                    color = badgeBgColor
                    style = Paint.Style.FILL
                    isAntiAlias = true
                }
                val pillBorderPaint = Paint().apply {
                    color = badgeBorderColor
                    style = Paint.Style.STROKE
                    strokeWidth = 0.8f
                    isAntiAlias = true
                }
                pillPaint.color = badgeTextColor
                pillPaint.textAlign = Paint.Align.CENTER

                canvas.drawRoundRect(pillRect, 9f, 9f, pillBgPaint)
                canvas.drawRoundRect(pillRect, 9f, 9f, pillBorderPaint)
                canvas.drawText(badgeText, pillRect.centerX(), pillRect.centerY() + 3.2f, pillPaint)

                yCursor += settleRowHeight + settleSpacing
            }
        }

        // 7. Footer Section
        val footerLineY = PAGE_HEIGHT - outerMargin - 32f
        val linePaint = Paint().apply {
            color = Color.parseColor("#334155")
            strokeWidth = 1f
            isAntiAlias = true
        }
        canvas.drawLine(contentLeft, footerLineY, contentRight, footerLineY, linePaint)

        val footerTextPaint = Paint().apply {
            color = Color.parseColor("#94A3B8")
            typeface = fontNormal
            textSize = 8.5f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("Design and programmed by Dipu • হিসাব Pro", PAGE_WIDTH / 2f, footerLineY + 14f, footerTextPaint)

        val timestamp = SimpleDateFormat("dd/MM/yyyy, hh:mm a", Locale.US).format(Date())
        val bengaliTimestamp = BengaliFormatter.toBengaliDigits(timestamp)
        val timePaint = Paint().apply {
            color = Color.parseColor("#64748B")
            typeface = fontNormal
            textSize = 7.5f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("রিপোর্ট তৈরির তারিখ ও সময়: $bengaliTimestamp", PAGE_WIDTH / 2f, footerLineY + 25f, timePaint)

        pdfDocument.finishPage(page)
        return pdfDocument
    }
}

/**
 * Custom [PrintDocumentAdapter] integrating native [PdfDocument] with [PrintManager].
 */
private class PdfReportPrintAdapter(
    private val context: Context,
    private val members: List<Member>,
    private val calculation: CalculationResult
) : PrintDocumentAdapter() {

    override fun onLayout(
        oldAttributes: PrintAttributes?,
        newAttributes: PrintAttributes?,
        cancellationSignal: CancellationSignal?,
        callback: LayoutResultCallback?,
        bundle: Bundle?
    ) {
        if (cancellationSignal?.isCanceled == true) {
            callback?.onLayoutCancelled()
            return
        }

        val info = PrintDocumentInfo.Builder("হিসাব_Pro_বাজার_খরচ_${System.currentTimeMillis()}.pdf")
            .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
            .setPageCount(1)
            .build()

        callback?.onLayoutFinished(info, true)
    }

    override fun onWrite(
        pages: Array<out PageRange>?,
        destination: ParcelFileDescriptor?,
        cancellationSignal: CancellationSignal?,
        callback: WriteResultCallback?
    ) {
        if (cancellationSignal?.isCanceled == true) {
            callback?.onWriteCancelled()
            return
        }

        var pdfDoc: PdfDocument? = null
        try {
            pdfDoc = PrintReportHelper.generatePdfDocument(context, members, calculation)
            FileOutputStream(destination?.fileDescriptor).use { output ->
                pdfDoc.writeTo(output)
            }
            callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
        } catch (e: Exception) {
            callback?.onWriteFailed(e.message)
        } finally {
            pdfDoc?.close()
        }
    }
}
