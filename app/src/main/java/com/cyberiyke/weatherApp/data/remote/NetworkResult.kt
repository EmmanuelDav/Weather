package com.cyberiyke.weatherApp.data.remote

sealed class NetworkResult {
    object Idle : NetworkResult()
    object Success : NetworkResult()
    data class Failure(val message: String) : NetworkResult()
}


