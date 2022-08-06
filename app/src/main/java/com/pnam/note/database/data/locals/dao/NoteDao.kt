package com.pnam.note.database.data.locals.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.pnam.note.database.data.locals.NoteLocals
import com.pnam.note.database.data.models.Note
import io.reactivex.rxjava3.core.Single

@Dao
interface NoteDao : NoteLocals {

    @Insert(onConflict = REPLACE)
    override fun addNote(note: Note)

    @Insert(onConflict = REPLACE)
    override fun addNote(notes: List<Note>)

    @Update
    override fun editNote(note: Note)

    @Delete
    override fun deleteNote(note: Note)

    @Query("SELECT * FROM Note ORDER BY edit_at DESC, create_at DESC LIMIT :limit OFFSET :page*:limit-:limit")
    override fun findNotes(page: Int, limit: Int): Single<MutableList<Note>>

    @Query(
        "SELECT * FROM Note " +
                "WHERE title LIKE '%' || :keySearch || '%' " +
                "OR `desc` LIKE '%' || :keySearch || '%' " +
                "ORDER BY edit_at DESC, create_at DESC"
    )
    override fun searchNotes(keySearch: String): Single<MutableList<Note>>

    @Query("SELECT * FROM Note WHERE note_id = :id")
    override fun findNoteDetail(id: String): Single<Note>

    @Query("DELETE FROM Note")
    fun deleteAllNote()
}