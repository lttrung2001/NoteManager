package com.pnam.note.database.data.locals

import com.pnam.note.database.data.models.Note
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface NoteLocals {
    fun findNotes(page: Int, limit: Int): Single<MutableList<Note>>
    fun findNoteDetail(id: String): Single<Note>
    fun addNote(note: Note): Completable
    fun addNote(notes: List<Note>): Completable
    fun editNote(note: Note): Completable
    fun deleteNote(note: Note): Completable
    fun searchNotes(keySearch: String): Single<MutableList<Note>>
}