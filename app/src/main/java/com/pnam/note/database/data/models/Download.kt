package com.pnam.note.database.data.models

data class Download (
    val id: Long,
    val name: String
        ) {
    companion object {
        val downloads: MutableList<Download> by lazy {
            mutableListOf()
        }
    }
}