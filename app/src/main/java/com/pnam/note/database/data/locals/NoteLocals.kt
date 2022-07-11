package com.pnam.note.database.data.locals

import com.pnam.note.database.data.models.Note
import com.pnam.note.database.data.models.PagingList
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface NoteLocals {
    fun findNotes(): Single<PagingList<Note>>
    fun findNoteDetail(): Single<Note>
    fun addNote(note: Note): Single<Note>
    fun editNote(note: Note): Single<Note>
    fun deleteNote(note: Note): Single<Note>
}