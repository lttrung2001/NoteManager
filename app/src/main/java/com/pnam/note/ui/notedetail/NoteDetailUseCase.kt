package com.pnam.note.ui.notedetail

import com.pnam.note.database.data.models.Note
import io.reactivex.rxjava3.core.Single

interface NoteDetailUseCase {
    fun getNoteDetail(): Single<Note>
    fun deleteNote(note: Note): Single<Note>
}