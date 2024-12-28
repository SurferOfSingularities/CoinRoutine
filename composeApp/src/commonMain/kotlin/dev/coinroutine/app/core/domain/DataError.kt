package dev.coinroutine.app.core.domain

sealed interface DataError : Error {
    enum class Remote : DataError {
        REQUEST_TIMEOUT,
        NO_INTERNET,
        TOO_MANY_REQUESTS,
        SERVER,
        SERIALIZATION,
        UNKNOWN
    }

    enum class Local : DataError {
        DISK_FULL,
        INSUFFICIENT_FUNDS,
        UNKNOWN
    }
}