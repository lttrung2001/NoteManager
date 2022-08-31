package com.pnam.note.ui.dashboard

import com.pnam.note.database.data.models.Note
import com.pnam.note.database.data.models.PagingList
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface DashboardUseCase {
    fun getNotes(page: Int, limit: Int): Single<PagingList<Note>>
    fun deleteNote(note: Note): Single<Note>
    fun searchNotes(keySearch: String): Single<MutableList<Note>>
    fun refreshNotes(page: Int, limit: Int): Single<List<Note>>
}