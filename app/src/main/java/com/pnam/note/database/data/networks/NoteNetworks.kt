package com.pnam.note.database.data.networks

import com.pnam.note.database.data.models.Note
import com.pnam.note.database.data.models.PagingList
import io.reactivex.rxjava3.core.Single

interface NoteNetworks {
    fun fetchNotes(): Single<PagingList<Note>>
    fun fetchNoteDetail(): Single<Note>
    fun addNote(note: Note): Single<Note>
    fun editNote(note: Note): Single<Note>
    fun deleteNote(note: Note): Single<Note>
}