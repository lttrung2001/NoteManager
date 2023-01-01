package com.pnam.note.database.data.locals.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Note(
    @PrimaryKey @ColumnInfo(name = "note_id") val id: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "desc") val description: String,
    @ColumnInfo(name = "edit_at") val editAt: Long,
    @ColumnInfo(name = "create_at") val createAt: Long,
    @ColumnInfo(name = "images") val images: List<String>?,
) : Serializable