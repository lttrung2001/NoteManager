package com.pnam.note.database.data.models

data class APIResult<E> (
    val code: Int,
    val message: String?,
    val data: E
        )