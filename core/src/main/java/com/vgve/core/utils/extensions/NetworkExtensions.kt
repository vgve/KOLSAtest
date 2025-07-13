package com.vgve.core.utils.extensions

import retrofit2.Response

class ApiException(message: String) : Throwable(message)

@Suppress("UNCHECKED_CAST")
fun <T> Response<T>.dataOrThrow(): T =
    if (isSuccessful) body() ?: true as T else throw ApiException(message())

fun String.decodeUnicodeEscapes(): String {
    val regex = "\\\\u([0-9a-fA-F]{4})".toRegex()
    return regex.replace(this) { matchResult ->
        matchResult.groupValues[1].toInt(16).toChar().toString()
    }
}
