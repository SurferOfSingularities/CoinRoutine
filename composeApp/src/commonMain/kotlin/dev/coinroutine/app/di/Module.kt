package dev.coinroutine.app.di

import androidx.room.RoomDatabase
import dev.coinroutine.app.coins.data.remote.impl.KtorCoinsRemoteDataSource
import dev.coinroutine.app.coins.domain.api.CoinsRemoteDataSource
import dev.coinroutine.app.coins.domain.usecase.GetCoinDetailsUseCase
import dev.coinroutine.app.coins.domain.usecase.GetCoinPriceHistoryUseCase
import dev.coinroutine.app.coins.domain.usecase.GetCoinsListUseCase
import dev.coinroutine.app.coins.presentation.CoinListViewModel
import dev.coinroutine.app.core.database.portfolio.PortfolioDatabase
import dev.coinroutine.app.core.database.portfolio.getPortfolioDatabase
import dev.coinroutine.app.core.network.HttpClientFactory
import io.ktor.client.HttpClient
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

fun initKoin(config: KoinAppDeclaration? = null) =
    startKoin {
        config?.invoke(this)
        modules(
            sharedModule,
            platformModule
        )
    }

expect val platformModule: Module

val sharedModule = module {
    // Core
    single<HttpClient> { HttpClientFactory.create(get()) }

    // Portfolio
    single {
        getPortfolioDatabase(get<RoomDatabase.Builder<PortfolioDatabase>>())
    }

    // Coins list
    viewModel { CoinListViewModel(get(), get()) }
    singleOf(::KtorCoinsRemoteDataSource).bind<CoinsRemoteDataSource>()
    singleOf(::GetCoinsListUseCase)
    singleOf(::GetCoinDetailsUseCase)
    singleOf(::GetCoinPriceHistoryUseCase)
}