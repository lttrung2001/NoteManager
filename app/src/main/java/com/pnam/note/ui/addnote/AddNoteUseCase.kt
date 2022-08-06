package com.pnam.note.ui.addnote

import com.pnam.note.database.data.models.Note
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface AddNoteUseCase {
    fun addNote(note: Note): Single<Note>
}