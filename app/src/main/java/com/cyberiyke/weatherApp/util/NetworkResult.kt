package com.cyberiyke.weatherApp.util

sealed class NetworkResult {
    object Idle : NetworkResult()
    object Success : NetworkResult()
    data class Failure(val message: String) : NetworkResult()
}


