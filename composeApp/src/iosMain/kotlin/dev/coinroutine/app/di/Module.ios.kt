package dev.coinroutine.app.di

import androidx.room.RoomDatabase
import dev.coinroutine.app.core.database.portfolio.PortfolioDatabase
import dev.coinroutine.app.core.database.portfolio.getPortfolioDatabase
import dev.coinroutine.app.core.database.portfolio.getPortfolioDatabaseBuilder
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule: Module = module {
    // core
    single<HttpClientEngine> { Darwin.create() }

    // Portfolio
    singleOf(::getPortfolioDatabaseBuilder).bind<RoomDatabase.Builder<PortfolioDatabase>>()
}