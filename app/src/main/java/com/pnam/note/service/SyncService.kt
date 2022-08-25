package com.pnam.note.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.pnam.note.database.data.locals.NoteLocals
import com.pnam.note.database.data.models.NoteAndStatus
import com.pnam.note.database.data.models.NoteStatus
import com.pnam.note.database.data.networks.NoteNetworks
import com.pnam.note.utils.RoomUtils.Companion.ADD_NOTE_STATUS
import com.pnam.note.utils.RoomUtils.Companion.DELETE_NOTE_STATUS
import com.pnam.note.utils.RoomUtils.Companion.EDIT_NOTE_STATUS
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SyncService : Service() {
    @Inject
    lateinit var noteLocals: NoteLocals
    @Inject
    lateinit var noteNetworks: NoteNetworks
    private lateinit var syncList: List<NoteAndStatus>

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        CoroutineScope(Dispatchers.IO).launch {
            syncList = noteLocals.findUnsyncNotes()
            if (syncList.isNotEmpty()) {
                for (i in syncList) {
                    when (i.status.status) {
                        ADD_NOTE_STATUS -> {
                            i.note?.let { localNote ->
                                noteNetworks.addNote(localNote)
                                    .subscribe({ successNote ->
                                        noteLocals.afterAddNoteOffline(localNote,successNote)
                                }, {
                                    onDestroy()
                                })
                            }
                        }
                        EDIT_NOTE_STATUS -> {
                            i.note?.let { localNote ->
                                noteNetworks.editNote(localNote).subscribe({ successNote ->
                                    noteLocals.aftereditNoteOffline(localNote, successNote)
                                }, {
                                    onDestroy()
                                })
                            }
                        }
                        else -> {
                            noteNetworks.deleteNote(i.status.id).subscribe({ successNote ->
                                noteLocals.afterDeleteNoteOffline(successNote)
                            }, {
                                onDestroy()
                            })
                        }
                    }
                }
            } else {
                onDestroy()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
}