package com.pnam.note.ui.dashboard

import com.pnam.note.database.data.models.Note
import com.pnam.note.database.data.models.PagingList
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface DashboardUseCase {
    fun getNotes(): Single<PagingList<Note>>
    fun deleteNote(note: Note): Single<Note>
}