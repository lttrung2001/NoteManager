package com.pnam.note.database.data.locals

import com.pnam.note.database.data.models.Note
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface NoteLocals {
    fun findNotes(page: Int, limit: Int): Single<MutableList<Note>>
    fun findNoteDetail(id: String): Single<Note>
    fun addNote(note: Note)
    fun addNote(notes: List<Note>)
    fun editNote(note: Note)
    fun deleteNote(note: Note)
    fun searchNotes(keySearch: String): Single<MutableList<Note>>
}