package com.pnam.note.database.data.models

data class APIResult (
    val code: Int,
    val message: String?,
    val data: Any
        )