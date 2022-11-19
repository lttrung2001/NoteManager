package com.pnam.note.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.pnam.note.database.data.locals.NoteLocals
import com.pnam.note.database.data.locals.entities.NoteAndStatus
import com.pnam.note.service.SyncService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NetworkReceiver : BroadcastReceiver() {
    @Inject
    lateinit var noteLocals: NoteLocals

    override fun onReceive(context: Context?, intent: Intent?) {
        if (ConnectivityManager.CONNECTIVITY_ACTION == intent?.action) {
            val syncIntent = Intent(context, SyncService::class.java)
            val unsyncNotes = arrayListOf<NoteAndStatus>()
            CoroutineScope(Dispatchers.IO).launch {
                unsyncNotes.addAll(noteLocals.findUnsyncNotes())
            }
            if (isNetworkAvailable(context) && unsyncNotes.isNotEmpty()) {
                /* Sync data here */
                context?.startService(syncIntent)
            } else {
                context?.stopService(syncIntent)
            }
        }
    }

    private fun isNetworkAvailable(context: Context?): Boolean {
        val connectivityManager = context
            ?.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null
                && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
    }
}