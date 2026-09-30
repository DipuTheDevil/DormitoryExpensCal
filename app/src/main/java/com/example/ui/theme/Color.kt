package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Dark Palette
val BgDarkNavy = Color(0xFF0F172A)
val BgLightNavy = Color(0xFF1E293B)

val CardBackground = Color(0xB81E293B)
val CardBorder = Color(0x26FFFFFF)
val CardRowBackground = Color(0x800F172A)
val CardRowBorder = Color(0x14FFFFFF)

val TextMain = Color(0xFFF8FAFC)
val TextMuted = Color(0xFF94A3B8)

val PrimaryIndigo = Color(0xFF4F46E5)
val PrimaryIndigoHover = Color(0xFF4338CA)
val AccentPurple = Color(0xFF8B5CF6)
val TitleGradientStart = Color(0xFF818CF8)
val TitleGradientEnd = Color(0xFFC084FC)

val CalcGreenStart = Color(0xFF10B981)
val CalcGreenEnd = Color(0xFF059669)

val TagGetBg = Color(0x2610B981)
val TagGetText = Color(0xFF6EE7B7)

val TagGiveBg = Color(0x26EF4444)
val TagGiveText = Color(0xFFFCA5A5)

val TagEvenBg = Color(0x2694A3B8)
val TagEvenText = Color(0xFFCBD5E1)

val DeleteBtnBg = Color(0x1AEF4444)
val DeleteBtnText = Color(0xFFEF4444)

val PrintBtnBg = Color(0x268B5CF6)
val PrintBtnBorder = Color(0x4D8B5CF6)
val PrintBtnText = Color(0xFFC084FC)

val SecondaryBtnBg = Color(0x0DFFFFFF)
val SecondaryBtnBorder = Color(0x1AFFFFFF)

val ResultsSectionBg = Color(0xCC0F172A)
val SettlementItemBg = Color(0x0DFFFFFF)
val SettlementItemBorder = Color(0x14FFFFFF)

val InputBg = Color(0x0DFFFFFF)
val InputBorder = Color(0x26FFFFFF)
val SummaryExpenseColor = Color(0xFF818CF8)
val SummaryPerPersonColor = Color(0xFFC084FC)

/**
 * Structured theme colors supporting dynamic Light and Dark mode switching.
 */
data class AppThemeColors(
    val isDark: Boolean,
    val bgGradientStart: Color,
    val bgGradientEnd: Color,
    val cardBackground: Color,
    val cardBorder: Color,
    val cardRowBackground: Color,
    val cardRowBorder: Color,
    val textMain: Color,
    val textMuted: Color,
    val inputBackground: Color,
    val inputBorder: Color,
    val inputFocusBorder: Color,
    val primaryIndigo: Color,
    val secondaryBtnBg: Color,
    val secondaryBtnBorder: Color,
    val secondaryBtnText: Color,
    val printBtnBg: Color,
    val printBtnBorder: Color,
    val printBtnText: Color,
    val resultsSectionBg: Color,
    val resultsSectionBorder: Color,
    val settlementItemBg: Color,
    val settlementItemBorder: Color,
    val tagGetBg: Color,
    val tagGetText: Color,
    val tagGiveBg: Color,
    val tagGiveText: Color,
    val tagEvenBg: Color,
    val tagEvenText: Color,
    val titleGradientStart: Color,
    val titleGradientEnd: Color,
    val summaryExpenseColor: Color,
    val summaryPerPersonColor: Color
)

val LightAppThemeColors = AppThemeColors(
    isDark = false,
    bgGradientStart = Color(0xFFF8FAFC), // Crisp slate 50
    bgGradientEnd = Color(0xFFE2E8F0),   // Slate 200
    cardBackground = Color(0xF5FFFFFF),  // Pure white card
    cardBorder = Color(0xFFCBD5E1),      // Subtle slate border
    cardRowBackground = Color(0xFFF1F5F9), // Slate 100
    cardRowBorder = Color(0xFFE2E8F0),
    textMain = Color(0xFF0F172A),        // Deep slate 900
    textMuted = Color(0xFF475569),       // Slate 600
    inputBackground = Color(0xFFFFFFFF), // Crisp white input
    inputBorder = Color(0xFFCBD5E1),
    inputFocusBorder = Color(0xFF4F46E5),
    primaryIndigo = Color(0xFF4F46E5),
    secondaryBtnBg = Color(0xFFE2E8F0),
    secondaryBtnBorder = Color(0xFFCBD5E1),
    secondaryBtnText = Color(0xFF1E293B),
    printBtnBg = Color(0xFFEDE9FE),
    printBtnBorder = Color(0xFFDDD6FE),
    printBtnText = Color(0xFF6D28D9),
    resultsSectionBg = Color(0xFFF8FAFC),
    resultsSectionBorder = Color(0xFFE2E8F0),
    settlementItemBg = Color(0xFFFFFFFF),
    settlementItemBorder = Color(0xFFE2E8F0),
    tagGetBg = Color(0xFFDCFCE7),
    tagGetText = Color(0xFF15803D),
    tagGiveBg = Color(0xFFFEE2E2),
    tagGiveText = Color(0xFFB91C1C),
    tagEvenBg = Color(0xFFF1F5F9),
    tagEvenText = Color(0xFF475569),
    titleGradientStart = Color(0xFF4338CA),
    titleGradientEnd = Color(0xFF7C3AED),
    summaryExpenseColor = Color(0xFF4338CA),
    summaryPerPersonColor = Color(0xFF7C3AED)
)

val DarkAppThemeColors = AppThemeColors(
    isDark = true,
    bgGradientStart = BgDarkNavy,
    bgGradientEnd = BgLightNavy,
    cardBackground = CardBackground,
    cardBorder = CardBorder,
    cardRowBackground = CardRowBackground,
    cardRowBorder = CardRowBorder,
    textMain = TextMain,
    textMuted = TextMuted,
    inputBackground = InputBg,
    inputBorder = InputBorder,
    inputFocusBorder = PrimaryIndigo,
    primaryIndigo = PrimaryIndigo,
    secondaryBtnBg = SecondaryBtnBg,
    secondaryBtnBorder = SecondaryBtnBorder,
    secondaryBtnText = TextMain,
    printBtnBg = PrintBtnBg,
    printBtnBorder = PrintBtnBorder,
    printBtnText = PrintBtnText,
    resultsSectionBg = ResultsSectionBg,
    resultsSectionBorder = CardRowBorder,
    settlementItemBg = SettlementItemBg,
    settlementItemBorder = SettlementItemBorder,
    tagGetBg = TagGetBg,
    tagGetText = TagGetText,
    tagGiveBg = TagGiveBg,
    tagGiveText = TagGiveText,
    tagEvenBg = TagEvenBg,
    tagEvenText = TagEvenText,
    titleGradientStart = TitleGradientStart,
    titleGradientEnd = TitleGradientEnd,
    summaryExpenseColor = SummaryExpenseColor,
    summaryPerPersonColor = SummaryPerPersonColor
)
