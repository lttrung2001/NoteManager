package com.pnam.note.ui.dashboard

import com.pnam.note.database.data.models.Note
import com.pnam.note.database.data.models.PagingList
import com.pnam.note.database.repositories.NoteRepositories
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class DashboardUseCaseImpl @Inject constructor(
    private val repositories: NoteRepositories
) : DashboardUseCase {
    override fun getNotes(): Single<PagingList<Note>> {
        return repositories.getNotes()
    }

    override fun deleteNote(note: Note): Single<Note> {
        TODO("Not yet implemented")
    }
}