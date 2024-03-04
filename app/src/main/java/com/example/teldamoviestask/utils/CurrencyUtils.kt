package com.example.teldamoviestask.utils

import androidx.databinding.BindingAdapter
import java.text.NumberFormat
import java.util.Locale

object NumberUtils {
    fun formatToDollarWithSuffix(amount: Int): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale.US)
        return when {
            amount >= 1_000_000 -> {
                val formatted = formatter.format(amount / 1_000_000.0)
                "${formatted.dropLast(3)}M" // Drop the decimal part for millions
            }
            amount >= 1_000 -> {
                val formatted = formatter.format(amount / 1_000.0)
                "${formatted.dropLast(3)}K" // Drop the decimal part for thousands
            }
            else -> formatter.format(amount.toDouble())
        }
    }
}
