package com.pnam.note.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.pnam.note.database.data.locals.NoteLocals
import com.pnam.note.database.data.models.Note
import com.pnam.note.database.data.models.NoteStatus
import com.pnam.note.database.data.models.NoteAndStatus
import com.pnam.note.database.data.networks.NoteNetworks
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SyncService : Service() {
    @Inject lateinit var noteLocals: NoteLocals
    @Inject lateinit var noteNetworks: NoteNetworks
    private lateinit var syncList: List<NoteAndStatus>

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        CoroutineScope(Dispatchers.IO).launch {
            syncList = noteLocals.findNotesWithStatus()
            if (syncList.isNotEmpty()) {
                for (note in syncList) {
                    val tmpNote =
                        Note(note.id, note.title, note.description, note.createAt, note.editAt)
                    when (note.status) {
                        1 -> {
                            noteNetworks.addNote(tmpNote).doOnSuccess {
                                noteLocals.deleteNote(tmpNote)
                                noteLocals.addNote(it)
                            }
                        }
                        2 -> {
                            noteNetworks.editNote(tmpNote).subscribe()
                        }
                        else -> {
                            noteNetworks.deleteNote(tmpNote).subscribe()
                        }
                    }
                    noteLocals.deleteNoteStatus(NoteStatus(note.id, note.status))
                }
            } else {
                onDestroy()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
}