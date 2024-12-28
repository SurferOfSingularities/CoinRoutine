package dev.coinroutine.app.coins.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coinroutine.composeapp.generated.resources.Res
import coinroutine.composeapp.generated.resources.error_serialization
import dev.coinroutine.app.coins.domain.usecase.GetCoinPriceHistoryUseCase
import dev.coinroutine.app.coins.domain.usecase.GetCoinsListUseCase
import dev.coinroutine.app.core.domain.DataError
import dev.coinroutine.app.core.domain.Result
import dev.coinroutine.app.core.domain.coin.Coin
import dev.coinroutine.app.core.domain.onSuccess
import dev.coinroutine.app.core.util.formatFiat
import dev.coinroutine.app.core.util.formatPercentage
import dev.coinroutine.app.core.util.toUiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CoinListViewModel(
    private val getCoinsListUseCase: GetCoinsListUseCase,
    private val getCoinPriceHistoryUseCase: GetCoinPriceHistoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CoinState())
    internal val uiState = _uiState
        .onStart {
            getAllCoins()
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = CoinState()
        )

    private suspend fun getAllCoins() {
        when (val response = getCoinsListUseCase()) {
            is Result.Success -> _uiState.update {
                CoinState(
                    coins = response.data.map { coinItem ->
                        UiCoinListItem(
                            id =  coinItem.coin.id,
                            name = coinItem.coin.name,
                            symbol = coinItem.coin.symbol,
                            iconUrl = coinItem.coin.iconUrl,
                            formattedPrice = formatFiat(coinItem.price),
                            formattedChange = formatPercentage(coinItem.change),
                            isPositive = coinItem.change >= 0
                        )
                    }
                )
            }
            is Result.Error -> _uiState.update {
                it.copy(
                    error = response.error.toUiText()
                )
            }
        }
    }

    internal fun onCoinLongPress(coinId: String) {
        _uiState.update {
            it.copy(
                uiChartState = UiChartState(
                    sparkLine = emptyList(),
                    isLoading = true,
                )
            )
        }

        viewModelScope.launch {
            when (val response = getCoinPriceHistoryUseCase(coinId)) {
                is Result.Success -> {
                    val sortedPriceList = response.data.sortedBy { it.timeStamp }.map { it.price }
                    _uiState.update {
                        it.copy(
                            uiChartState = UiChartState(
                                sparkLine = sortedPriceList,
                                isLoading = false,
                                coinName = _uiState.value.coins.find { it.id == coinId }?.name.orEmpty()
                            )
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            error = response.error.toUiText(),
                            uiChartState = null
                        )
                    }
                }
            }
        }
    }

    internal fun onDismissChart() {
        _uiState.update {
            it.copy(
                uiChartState = null
            )
        }
    }

}