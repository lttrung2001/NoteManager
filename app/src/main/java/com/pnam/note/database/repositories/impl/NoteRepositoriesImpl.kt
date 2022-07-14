package com.pnam.note.database.repositories.impl

import com.pnam.note.database.data.locals.LoginLocals
import com.pnam.note.database.data.locals.NoteLocals
import com.pnam.note.database.data.models.Note
import com.pnam.note.database.data.models.PagingList
import com.pnam.note.database.data.networks.LoginNetworks
import com.pnam.note.database.data.networks.NoteNetworks
import com.pnam.note.database.repositories.NoteRepositories
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class NoteRepositoriesImpl @Inject constructor(
    override val locals: NoteLocals,
    override val networks: NoteNetworks
) : NoteRepositories {
    override fun getNotes(): Single<PagingList<Note>> {
        return networks.fetchNotes()
    }

    override fun getNoteDetail(): Single<Note> {
        TODO("Not yet implemented")
    }

    override fun addNote(note: Note): Single<Note> {
        TODO("Not yet implemented")
    }

    override fun editNote(note: Note): Single<Note> {
        TODO("Not yet implemented")
    }

    override fun deleteNote(note: Note): Single<Note> {
        TODO("Not yet implemented")
    }

}