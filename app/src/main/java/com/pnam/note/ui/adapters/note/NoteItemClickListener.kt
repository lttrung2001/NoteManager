package com.pnam.note.ui.adapters.note

import android.view.View
import com.pnam.note.database.data.locals.entities.Note

interface NoteItemClickListener {
    fun onClick(note: Note, view: View, position: Int)
    fun onDeleteClick(note: Note, position: Int)
}