package com.pnam.note.database.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NoteStatus(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "status") val status: Int
)
