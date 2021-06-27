package com.github.code.gambit.data.remote

import com.github.code.gambit.data.remote.responses.ListResponse
import com.github.code.gambit.data.remote.responses.Response
import com.github.code.gambit.utility.InternalServerException
import com.github.code.gambit.utility.sharedpreference.LastEvaluatedKeyManager
import java.lang.IllegalStateException

suspend fun <T> apiRequest(
    lekManager: LastEvaluatedKeyManager,
    keyType: LastEvaluatedKeyManager.KeyType,
    call: suspend (lek: String) -> ListResponse<T>
): List<T> {
    val response = call.invoke(lekManager.getLastEvalKey(keyType))
    response.body?.let { body ->
        body.lastEvaluatedKey?.let { lekManager.putLastEvalKey(it, keyType) }
        return body.items
    } ?: response.error?.let { throw InternalServerException(it) }
        ?: throw IllegalStateException("Internal Server Error V2")
}

suspend fun <T> apiRequest(
    call: suspend () -> Response<T>
): T {
    val response = call.invoke()
    response.body?.let { body ->
        return body
    } ?: response.error?.let { throw InternalServerException(it) }
        ?: throw IllegalStateException("Internal Server Error V2")
}
