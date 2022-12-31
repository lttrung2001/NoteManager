package com.pnam.note.database.data.models

import androidx.room.Embedded
import androidx.room.Relation
import com.pnam.note.database.data.models.Note
import com.pnam.note.database.data.models.NoteStatus

data class NoteAndStatus(
    @Embedded val status: NoteStatus,
    @Relation(parentColumn = "id", entityColumn = "note_id")
    val note: Note?
)