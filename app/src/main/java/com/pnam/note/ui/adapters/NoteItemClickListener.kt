package com.pnam.note.ui.adapters

import android.view.View
import com.pnam.note.database.data.models.Note

interface NoteItemClickListener {
    fun onClick(note: Note, view: View, position: Int)
    fun onDeleteClick(note: Note, position: Int)
}