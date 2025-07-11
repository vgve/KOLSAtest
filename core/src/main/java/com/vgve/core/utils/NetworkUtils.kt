package com.vgve.core.utils

import retrofit2.Response

class ApiException(message: String) : Throwable(message)

fun <T> Response<T>.dataOrThrow(): T =
    if (isSuccessful) body() ?: true as T else throw ApiException(message())
