package com.cyberiyke.weatherApp.util

sealed class NetworkResult<T> {
    class Loading<T> : NetworkResult<T>()

    data class Success<T>(val data: T) : NetworkResult<T>()

    data class Error<T>(val message: String) : NetworkResult<T>()

    companion object {
        fun <T> loading() = Loading<T>()
        fun <T> success(data: T) = Success(data)
        fun <T> error(message: String) = Error<T>(message)
    }
}


