package com.pnam.note.database.repositories.impl

import com.pnam.note.database.data.locals.NoteLocals
import com.pnam.note.database.data.models.Note
import com.pnam.note.database.data.models.PagingList
import com.pnam.note.database.data.networks.NoteNetworks
import com.pnam.note.database.repositories.NoteRepositories
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class NoteRepositoriesImpl @Inject constructor(
    override val locals: NoteLocals,
    override val networks: NoteNetworks
) : NoteRepositories {
    override fun getNotes(page: Int, limit: Int): Single<PagingList<Note>> {
        return networks.fetchNotes(page, limit).doOnSuccess {
            locals.addNote(it.data)
        }
    }

    override fun getNoteDetail(): Single<Note> {
        TODO("Not yet implemented")
    }

    override fun addNote(note: Note): Single<Note> {
        return networks.addNote(note).doOnSuccess {
            locals.addNote(note)
        }
    }

    override fun editNote(note: Note): Single<Note> {
        return networks.editNote(note).doOnSuccess {
            locals.editNote(it)
        }
    }

    override fun deleteNote(note: Note): Single<Note> {
        return networks.deleteNote(note).doOnSuccess {
            locals.deleteNote(it)
        }
    }

    override fun searchNotes(keySearch: String): Single<MutableList<Note>> {
        return locals.searchNotes(keySearch)
    }
}