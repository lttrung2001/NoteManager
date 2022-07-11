package com.pnam.note.database.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey @ColumnInfo(name = "note_id") val id: String,
    @ColumnInfo(name = "note_title") val title: String,
    @ColumnInfo(name = "note_desc") val description: String,
    @ColumnInfo(name = "note_create_at") val createAt: Long,
    @ColumnInfo(name = "note_edit_at") var editAt: Long
)