package com.pnam.note.database.data.models

data class PagingList<E>(
    val data: List<E>,
    val hasNextPage: Boolean,
    val hasPrePage: Boolean
)