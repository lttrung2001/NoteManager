package com.pnam.note.ui.notedetail

import com.pnam.note.database.data.locals.entities.Note
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface NoteDetailUseCase {
    fun getNoteDetail(id: String): Single<Note>
    fun deleteNote(note: Note): Single<Note>
}