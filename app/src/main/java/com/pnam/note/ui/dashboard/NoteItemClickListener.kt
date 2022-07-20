package com.pnam.note.ui.dashboard

import android.view.View
import com.pnam.note.database.data.models.Note

interface NoteItemClickListener {
    fun onClick(note: Note, view: View)
}