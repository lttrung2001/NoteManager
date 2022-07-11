package com.pnam.note.utils

sealed interface Resource<T> {
    class Success<T>(val data: T) : Resource<T>
    class Loading<T>(val data: T? = null) : Resource<T>
    class Error<T>(val message: String, val data: T? = null) : Resource<T>
}
