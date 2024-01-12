package com.example.shopmate.utils

sealed class Response<T>(val errorMessage: String? = null){
    class Success<T>(): Response<T>()
    class Failure<T>(errorMessage: String): Response<T>(errorMessage = errorMessage)
}
