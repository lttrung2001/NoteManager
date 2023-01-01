package com.pnam.note.ui.addnote

import com.pnam.note.database.data.locals.entities.Note
import com.pnam.note.database.repositories.NoteRepositories
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class AddNoteUseCaseImpl @Inject constructor(
    private val repositories: NoteRepositories
) : AddNoteUseCase {
    override fun addNote(note: Note): Single<Note> {
        return repositories.addNote(note)
    }
}