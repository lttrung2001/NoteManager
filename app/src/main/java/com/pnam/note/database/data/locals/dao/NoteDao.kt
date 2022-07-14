package com.pnam.note.database.data.locals.dao

import androidx.room.*
import com.pnam.note.database.data.locals.NoteLocals
import com.pnam.note.database.data.models.Note
import com.pnam.note.database.data.models.PagingList
import io.reactivex.rxjava3.core.Single

@Dao
interface NoteDao : NoteLocals {
    @Query("SELECT * FROM notes")
    override fun findNotes(): Single<PagingList<Note>> {
        TODO("Not yet implemented")
    }

    @Query("SELECT * FROM notes")
    override fun findNoteDetail(): Single<Note> {
        TODO("Not yet implemented")
    }

    @Insert
    override fun addNote(note: Note): Single<Note> {
        TODO("Not yet implemented")
    }

    @Update
    override fun editNote(note: Note): Single<Note> {
        TODO("Not yet implemented")
    }

    @Delete
    override fun deleteNote(note: Note): Single<Note> {
        TODO("Not yet implemented")
    }
}