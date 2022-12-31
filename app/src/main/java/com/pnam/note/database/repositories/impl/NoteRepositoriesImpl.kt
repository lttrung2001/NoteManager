package com.pnam.note.database.repositories.impl

import com.pnam.note.database.data.locals.NoteLocals
import com.pnam.note.database.data.models.Note
import com.pnam.note.database.data.models.PagingList
import com.pnam.note.database.data.networks.NoteNetworks
import com.pnam.note.database.repositories.NoteRepositories
import io.reactivex.rxjava3.core.Single
import java.io.File
import javax.inject.Inject

class NoteRepositoriesImpl @Inject constructor(
    override val locals: NoteLocals,
    override val networks: NoteNetworks
) : NoteRepositories {
    override fun getNotes(page: Int, limit: Int): Single<PagingList<Note>> {
        return networks.fetchNotes(page, limit).doOnSuccess { paging ->
            locals.addNote(paging.data)
        }
    }

    override fun refreshNotes(page: Int, limit: Int): Single<PagingList<Note>> {
        return networks.refreshNotes(page, limit).doOnSuccess { paging ->
            locals.addNote(paging.data)
        }
    }

    override fun getNoteDetail(id: String): Single<Note> {
        return networks.fetchNoteDetail(id).doOnSuccess {
            locals.editNote(it)
        }
    }

    override fun addNote(note: Note): Single<Note> {
        return networks.addNote(note).doOnSuccess {
            locals.addNote(it)
        }
    }

    override fun editNote(note: Note): Single<Note> {
        return networks.editNote(note).doOnSuccess {
            locals.editNote(it)
        }
    }

    override fun deleteNote(note: Note): Single<Note> {
        return networks.deleteNote(note.id).doOnSuccess {
            locals.deleteNote(it)
        }
    }

    override fun searchNotes(keySearch: String): Single<MutableList<Note>> {
        return locals.searchNotes(keySearch)
    }
}