package com.pnam.note.database.data.locals.entities

import androidx.room.Embedded
import androidx.room.Relation
import com.pnam.note.database.data.models.Note

data class NoteAndStatus(
    @Embedded val status: NoteStatus,
    @Relation(parentColumn = "id", entityColumn = "note_id")
    val note: Note?
)