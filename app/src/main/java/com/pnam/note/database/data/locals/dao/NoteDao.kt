package com.pnam.note.database.data.locals.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.pnam.note.database.data.locals.NoteLocals
import com.pnam.note.database.data.models.Note
import com.pnam.note.database.data.models.NoteAndStatus
import com.pnam.note.database.data.models.NoteStatus
import com.pnam.note.utils.RoomUtils.Companion.ADD_NOTE_STATUS
import com.pnam.note.utils.RoomUtils.Companion.DELETE_NOTE_STATUS
import com.pnam.note.utils.RoomUtils.Companion.EDIT_NOTE_STATUS
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

    @Query("SELECT * FROM Note WHERE note_id = :id LIMIT 1")
    override fun findNoteDetail(id: String): Single<Note>

    @Query("DELETE FROM Note")
    override fun deleteAllNote()


    @Insert(onConflict = REPLACE)
    override fun addNoteStatus(noteStatus: NoteStatus)

    @Delete
    override fun deleteNoteStatus(noteStatus: NoteStatus)

    @Query("DELETE FROM NoteStatus")
    override fun deleteAllNoteStatus()

    @Query("SELECT * FROM NoteStatus WHERE id = :id LIMIT 1")
    fun findNoteStatusById(id: String): List<NoteStatus>

    @Transaction
    override fun addNoteAndStatus(note: Note) {
        addNote(note)
        addNoteStatus(NoteStatus(note.id, ADD_NOTE_STATUS))
    }

    @Transaction
    override fun editNoteAndStatus(note: Note) {
        editNote(note)
        if (findNoteStatusById(note.id).isEmpty()) {
            addNoteStatus(NoteStatus(note.id, EDIT_NOTE_STATUS))
        }
    }

    @Transaction
    override fun deleteNoteAndStatus(note: Note) {
        deleteNote(note)
        val list = findNoteStatusById(note.id)
        if (list.isEmpty()) {
            addNoteStatus(NoteStatus(note.id, DELETE_NOTE_STATUS))
        } else if (list[0].status == ADD_NOTE_STATUS) {
            deleteNoteStatus(NoteStatus(note.id, ADD_NOTE_STATUS))
        } else if (list[0].status == EDIT_NOTE_STATUS) {
            addNoteStatus(NoteStatus(note.id, DELETE_NOTE_STATUS))
        }
    }

    @Transaction
    @Query("SELECT * FROM NoteStatus")
    override fun findNotesAndStatus(): List<NoteAndStatus>
}