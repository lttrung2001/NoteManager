package com.pnam.note.database.data.networks

import com.pnam.note.database.data.models.Note
import com.pnam.note.database.data.models.PagingList
import io.reactivex.rxjava3.core.Single
import java.io.File
import javax.inject.Singleton

@Singleton
interface NoteNetworks {
    fun fetchNotes(page: Int, limit: Int): Single<PagingList<Note>>
    fun refreshNotes(page: Int, limit: Int): Single<PagingList<Note>>
    fun fetchNoteDetail(): Single<Note>
    fun addNote(note: Note): Single<Note>
    fun editNote(note: Note): Single<Note>
    fun deleteNote(id: String): Single<Note>
}