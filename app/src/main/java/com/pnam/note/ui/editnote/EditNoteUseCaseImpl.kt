package com.pnam.note.ui.editnote

import com.pnam.note.database.data.locals.entities.Note
import com.pnam.note.database.repositories.ImageRepositories
import com.pnam.note.database.repositories.NoteRepositories
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class EditNoteUseCaseImpl @Inject constructor(
    private val repositories: NoteRepositories,
    private val imageRepositories: ImageRepositories
) : EditNoteUseCase {
    override fun editNote(note: Note): Single<Note> {
        return repositories.editNote(note)
    }
}