package edu.sdgku.stepcounter.ui.dashboard

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass

fun WindowWidthSizeClass.isWide(): Boolean {
    // if this is not a phone size  return this size
    return this != WindowWidthSizeClass.Compact
}

fun WindowWidthSizeClass.label(): String {
    return when (this) {
        WindowWidthSizeClass.Compact -> "Compact - mobile"
        WindowWidthSizeClass.Medium -> "Medium - Tablet"
        else -> "Expanded - Desktop"
    }
}
