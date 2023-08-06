package com.anyandroid.authusingphone

sealed class Resources<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T, message: String) : Resources<T>(data, message)
    class Failed<T>(message: String, data: T? = null) : Resources<T>(data, message)
    class Loading<T> : Resources<T>()
    class Unspecified<T> : Resources<T>()

}