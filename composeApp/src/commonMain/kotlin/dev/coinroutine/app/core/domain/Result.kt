package dev.coinroutine.app.core.domain

import coinroutine.composeapp.generated.resources.Res

sealed interface Result<out D, out E: Error> {
    data class Success<out D>(val data: D): Result<D, Nothing>
    data class Error<out E: dev.coinroutine.app.core.domain.Error>(val error: E): Result<Nothing, E>
}

typealias EmptyResult<E> = Result<Unit, E>

inline fun <T, E: Error, R> Result<T, E>.map(map: (T) -> R): Result<R, E> {
    return when(this) {
        is Result.Success -> Result.Success(map(data))
        is Result.Error -> Result.Error(error)
    }
}

inline fun <T, E: Error> Result<T, E>.onSuccess(action: (T) -> Unit): Result<T, E> = when (this) {
    is Result.Error -> this
    is Result.Success -> {
        action(data)
        this
    }
}

fun <T, E: Error> Result<T, E>.asEmptyDataResult(): EmptyResult<E> = map {

}

inline fun <T, E: Error> Result<T, E>.onError(action: (E) -> Unit): Result<T, E> = when (this) {
    is Result.Error -> {
        action(error)
        this
    }
    is Result.Success -> this
}

