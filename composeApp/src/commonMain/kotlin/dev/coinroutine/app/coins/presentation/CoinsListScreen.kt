package dev.coinroutine.app.coins.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import dev.coinroutine.app.coins.presentation.component.PerformanceChart
import dev.coinroutine.app.theme.LocalCoinRoutineColorsPalette
import dev.coinroutine.app.theme.OnPrimaryDark
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun CoinsListScreen(
    onCoinClicked: (String) -> Unit
) {

    val viewModel = koinViewModel<CoinListViewModel>()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    CoinsListContent(
        state = state,
        onCoinClicked = onCoinClicked,
        onCoinLongPress = viewModel::onCoinLongPress,
        onDismissChart = viewModel::onDismissChart
    )

}

@Composable
internal fun CoinsListContent(
    state: CoinState,
    onCoinClicked: (String) -> Unit,
    onCoinLongPress: (String) -> Unit,
    onDismissChart: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        if (state.uiChartState != null) {
            CoinChartDialog(
                uiChartState = state.uiChartState,
                onDismiss = { onDismissChart() }
            )
        }

        CoinsList(
            coins = state.coins,
            onCoinClicked = onCoinClicked,
            onCoinLongPress = onCoinLongPress
        )

    }

}

@Composable
internal fun CoinsList (
    coins: List<UiCoinListItem>,
    onCoinClicked: (String) -> Unit,
    onCoinLongPress: (String) -> Unit,
) {

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
    ) {

        LazyColumn(
            modifier = Modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item {
                Text(
                    text = "🔥Top Coins🔥",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    modifier = Modifier.padding(16.dp)
                )
            }

            items(coins) { coin ->
                CoinListItem(
                    coin = coin,
                    onCoinClicked = onCoinClicked,
                    onCoinLongPress = onCoinLongPress
                )
            }
        }

    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun CoinListItem(
    coin: UiCoinListItem,
    onCoinClicked: (String) -> Unit,
    onCoinLongPress: (String) -> Unit,
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onCoinClicked(coin.id)
            }
            .combinedClickable(
                onLongClick = { onCoinLongPress(coin.id) },
                onClick = { onCoinClicked(coin.id) }
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = coin.iconUrl,
            contentDescription = null,
            modifier = Modifier.padding(6.dp).clip(CircleShape).size(40.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f),
        ) {

            Text(
                text = coin.name,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = coin.symbol,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            )
        }

        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = coin.formattedPrice,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = coin.formattedChange,
                color = if (coin.isPositive) LocalCoinRoutineColorsPalette.current.profitGreen
                        else LocalCoinRoutineColorsPalette.current.lossRed,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            )
        }
    }
}

@Composable
private fun CoinChartDialog(
    uiChartState: UiChartState,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        modifier = Modifier.fillMaxWidth(),
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "24h Price Chart for ${uiChartState.coinName}"
            )
        },
        text = {
           if (uiChartState.isLoading) {
               Box(
                   modifier = Modifier.fillMaxSize(),
                   contentAlignment = Alignment.Center
               ) {
                   CircularProgressIndicator(modifier = Modifier.size(32.dp))
               }
           } else {
               PerformanceChart(
                   modifier = Modifier
                       .fillMaxWidth()
                       .height(200.dp)
                       .padding(16.dp),
                   nodes = uiChartState.sparkLine
               )
           }
        },
        confirmButton = {},
        dismissButton = {
            Button(
                onClick = { onDismiss() },
            ) {
                Text(
                    text = "Close"
                )
            }
        }
    )
}

