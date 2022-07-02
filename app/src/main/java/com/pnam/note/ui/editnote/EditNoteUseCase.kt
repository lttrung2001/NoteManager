package com.pnam.note.ui.editnote

import com.pnam.note.database.data.models.Note
import io.reactivex.rxjava3.core.Single

interface EditNoteUseCase {
    fun editNote(note: Note): Single<Note>
}