package com.pnam.note.database.repositories

import com.pnam.note.database.data.locals.LoginLocals
import com.pnam.note.database.data.locals.NoteLocals
import com.pnam.note.database.data.models.Note
import com.pnam.note.database.data.models.PagingList
import com.pnam.note.database.data.networks.LoginNetworks
import com.pnam.note.database.data.networks.NoteNetworks
import io.reactivex.rxjava3.core.Single

interface NoteRepositories {
    val locals: NoteLocals
    val networks: NoteNetworks
    fun getNotes(): Single<PagingList<Note>>
    fun getNoteDetail(): Single<Note>
    fun addNote(note: Note): Single<Note>
    fun editNote(note: Note): Single<Note>
    fun deleteNote(note: Note): Single<Note>
}