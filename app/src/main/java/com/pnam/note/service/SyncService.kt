package com.pnam.note.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.pnam.note.R
import com.pnam.note.database.data.locals.NoteLocals
import com.pnam.note.database.data.models.NoteAndStatus
import com.pnam.note.database.data.networks.NoteNetworks
import com.pnam.note.utils.AppConstants.SYNC_CHANNEL_ID
import com.pnam.note.utils.AppConstants.SYNC_NOTIFICATION_ID
import com.pnam.note.utils.RoomUtils.Companion.ADD_NOTE_STATUS
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
        val builder = NotificationCompat.Builder(this, SYNC_CHANNEL_ID).apply {
            setContentTitle("Sync notes")
            setContentText("Sync in progress")
            setSmallIcon(R.drawable.ic_change)
            priority = NotificationCompat.PRIORITY_HIGH
        }
        val PROGRESS_MAX = 100
        val PROGRESS_CURRENT = 0
        NotificationManagerCompat.from(this).apply {
            // Issue the initial notification with zero progress
            builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false)
            notify(SYNC_NOTIFICATION_ID, builder.build())

            // Do the job here that tracks the progress.
            // Usually, this should be in a
            // worker thread
            // To show progress, update PROGRESS_CURRENT and update the notification with:
            // builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
            // notificationManager.notify(notificationId, builder.build());
            CoroutineScope(Dispatchers.IO).launch {
                syncList = noteLocals.findUnsyncNotes()
                var currentIndex = 1.0
                if (syncList.isNotEmpty()) {
                    for (i in syncList) {
                        when (i.status.status) {
                            ADD_NOTE_STATUS -> {
                                i.note?.let { localNote ->
                                    noteNetworks.addNote(localNote)
                                        .subscribe({ successNote ->
                                            noteLocals.afterAddNoteOffline(localNote, successNote)
                                        }, {
                                            onDestroy()
                                        })
                                }
                            }
                            EDIT_NOTE_STATUS -> {
                                i.note?.let { localNote ->
                                    noteNetworks.editNote(localNote).subscribe({ successNote ->
                                        noteLocals.afterEditNoteOffline(localNote, successNote)
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
                        builder.setProgress(
                            PROGRESS_MAX,
                            (currentIndex / syncList.size * PROGRESS_MAX).toInt(), false
                        )
                        builder.setContentText("${(currentIndex++ / syncList.size * PROGRESS_MAX).toInt()}%")
                        notify(SYNC_NOTIFICATION_ID, builder.build())
                    }
                    // When done, update the notification one more time to remove the progress bar
                    builder.setContentText("Sync complete")
                        .setProgress(0, 0, false)
                    notify(SYNC_NOTIFICATION_ID, builder.build())
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
}