package dev.coinroutine.app.coins.presentation

import androidx.compose.runtime.Stable

@Stable
data class UiCoinListItem(
    val id: String,
    val name: String,
    val symbol: String,
    val iconUrl: String,
    val formattedPrice: String,
    val formattedChange: String,
    val isPositive: Boolean,
)
