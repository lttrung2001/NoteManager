package com.pnam.note.ui.notedetail

import com.pnam.note.database.data.models.Note
import com.pnam.note.database.repositories.NoteRepositories
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class NoteDetailUseCaseImpl @Inject constructor(
    private val repositories: NoteRepositories
) : NoteDetailUseCase {
    override fun getNoteDetail(id: String): Single<Note> {
        return repositories.getNoteDetail(id)
    }

    override fun deleteNote(note: Note): Single<Note> {
        TODO("Not yet implemented")
    }
}