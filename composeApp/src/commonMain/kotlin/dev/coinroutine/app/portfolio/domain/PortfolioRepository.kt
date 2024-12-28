package dev.coinroutine.app.portfolio.domain

import dev.coinroutine.app.core.domain.DataError
import dev.coinroutine.app.core.domain.EmptyResult
import kotlinx.coroutines.flow.Flow
import dev.coinroutine.app.core.domain.Result as Result

interface PortfolioRepository {

    suspend fun initialiseBalance()
    fun getPortfolioCoinsFlow(): Flow<Result<List<PortfolioCoinModel>, DataError.Remote>>
    suspend fun getPortfolioCoin(coinId: String): Result<PortfolioCoinModel?, DataError.Remote>
    suspend fun savePortfolioCoin(portfolioCoinModel: PortfolioCoinModel): EmptyResult<DataError.Local>
    suspend fun removeCoinFromPortfolio(coinInd: String)

    fun calculateTotalPortfolioValue(): Flow<Result<Double, DataError.Remote>>
    fun totalBalanceFlow(): Flow<Result<Double, DataError.Remote>>
    fun cashBalanceFlow(): Flow<Double>
    suspend fun updateCashBalance(newBalance: Double)
}