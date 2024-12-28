package dev.coinroutine.app.core.network

import dev.coinroutine.app.core.domain.DataError
import dev.coinroutine.app.core.domain.Result
import io.ktor.client.call.body
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

suspend inline fun <reified T> safeCall(
    execute: () -> HttpResponse
): Result<T, DataError.Remote> {
    val response = try {
        execute()
    } catch (e: SocketTimeoutException) {
        return Result.Error(DataError.Remote.REQUEST_TIMEOUT)
    } catch (e: UnresolvedAddressException) {
        return Result.Error(DataError.Remote.NO_INTERNET)
    } catch (e: Exception) {
        currentCoroutineContext().ensureActive()
        return Result.Error(DataError.Remote.UNKNOWN)
    }

    return successToResult(response)
}

suspend inline fun <reified T> successToResult(
    response: HttpResponse
) : Result<T, DataError.Remote> {
    return when (response.status.value) {
        in 200 until 300 -> try {
            Result.Success(response.body<T>())
        } catch (e: Exception) {
            Result.Error(DataError.Remote.SERIALIZATION)
        }

        HttpStatusCode.RequestTimeout.value -> Result.Error(DataError.Remote.REQUEST_TIMEOUT)

        HttpStatusCode.TooManyRequests.value -> Result.Error(DataError.Remote.TOO_MANY_REQUESTS)

        in HttpStatusCode.InternalServerError.value until 599 -> Result.Error(DataError.Remote.SERVER)

        else -> Result.Error(DataError.Remote.UNKNOWN)
    }
}