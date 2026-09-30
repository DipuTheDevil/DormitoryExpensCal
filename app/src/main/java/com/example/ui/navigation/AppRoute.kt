package com.example.ui.navigation

/**
 * HashRouter-like screen route representation for in-memory navigation state.
 */
enum class AppRoute(val hash: String, val titleBengali: String) {
    CALCULATOR("#calculator", "ক্যালকুলেটর"),
    HISTORY("#history", "পূর্বের হিসাব");

    companion object {
        fun fromHash(hash: String?): AppRoute {
            return entries.firstOrNull { it.hash.equals(hash, ignoreCase = true) } ?: CALCULATOR
        }
    }
}
