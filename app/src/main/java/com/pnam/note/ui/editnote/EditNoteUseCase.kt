package com.pnam.note.ui.editnote

import com.pnam.note.database.data.locals.entities.Note
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface EditNoteUseCase {
    fun editNote(note: Note): Single<Note>
}