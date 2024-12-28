package dev.coinroutine.app.coins.domain.usecase

import dev.coinroutine.app.coins.data.mapper.toPriceModel
import dev.coinroutine.app.coins.domain.api.CoinsRemoteDataSource
import dev.coinroutine.app.coins.domain.model.PriceModel
import dev.coinroutine.app.core.domain.DataError
import dev.coinroutine.app.core.domain.Result
import dev.coinroutine.app.core.domain.map

class GetCoinPriceHistoryUseCase(private val client: CoinsRemoteDataSource) {

    suspend operator fun invoke(coinId: String): Result<List<PriceModel>, DataError.Remote> {
        return client.getPriceHistory(coinId).map { dto ->
            dto.data.history.map { history -> history.toPriceModel() }
        }
    }
}