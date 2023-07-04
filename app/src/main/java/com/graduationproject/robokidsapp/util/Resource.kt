package com.graduationproject.robokidsapp.util

sealed class Resource<out T> {
    object Loading: Resource<Nothing>()
    data class Success<out T>(val data: T): Resource<T>()
    data class Failure(val error: String?): Resource<Nothing>()
}
