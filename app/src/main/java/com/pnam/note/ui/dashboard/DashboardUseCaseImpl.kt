package com.pnam.note.ui.dashboard

import com.pnam.note.database.data.models.Note
import com.pnam.note.database.data.models.PagingList
import com.pnam.note.database.repositories.NoteRepositories
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class DashboardUseCaseImpl @Inject constructor(
    private val repositories: NoteRepositories
) : DashboardUseCase {
    override fun getNotes(page: Int, limit: Int): Single<PagingList<Note>> {
        return repositories.getNotes(page, limit)
    }

    override fun deleteNote(note: Note): Single<Note> {
        return repositories.deleteNote(note)
    }

    override fun searchNotes(keySearch: String): Single<MutableList<Note>> {
        return repositories.searchNotes(keySearch)
    }
}