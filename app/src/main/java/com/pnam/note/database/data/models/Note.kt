package com.pnam.note.database.data.models

data class Note(
    val id: String,
    val title: String,
    val description: String,
    val createAt: Long,
    var editAt: Long
)