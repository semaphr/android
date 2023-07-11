package com.semaphr.utils

sealed class KSResult<out T : Any> {
    data class Success<out T : Any>(val data: T) : KSResult<T>()
    data class Error(val exception: Exception)   : KSResult<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
        }
    }
}