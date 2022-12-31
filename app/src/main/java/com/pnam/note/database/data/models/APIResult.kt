package com.pnam.note.database.data.models

data class APIResult<T> (
    val code: Int,
    val message: String?,
    val data: T
        )