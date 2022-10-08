package com.pnam.note.database.repositories.impl

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.pnam.note.database.data.models.Download
import com.pnam.note.database.repositories.DownloadRepositories
import com.pnam.note.utils.DownloadInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.security.cert.CertPathValidatorException
import javax.inject.Inject

class DownloadRepositoriesImpl @Inject constructor(
    @ApplicationContext context: Context
) : DownloadRepositories {
    private val downloadManager by lazy {
        context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    }

    override fun download(url: String): Long {
        val uri = Uri.parse(url)
        val request = DownloadManager.Request(uri)

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setTitle(uri.lastPathSegment)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDescription("Android Data download using DownloadManager")
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
            File.separator + uri.lastPathSegment)
        /* Xếp download vào hàng đợi
        * downloadPreference là id của download */
        val downloadPreference = downloadManager.enqueue(request)
        Download.downloads.add(Download(downloadPreference, uri.lastPathSegment?: "Unknown name"))
        downloadIdList.add(downloadPreference)
        return downloadPreference
    }

    override fun getDownloadStatus(id: Long): DownloadInfo {
        val query = DownloadManager.Query()
        query.setFilterById(id)
        val cursor = downloadManager.query(query)
        if (cursor?.moveToFirst() == true) {
            return downloadStatus(cursor)
        }
        return DownloadInfo.NotHasInfo
    }

    private fun downloadStatus(cursor: Cursor): DownloadInfo {
        val columnStatusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
        val status = cursor.getInt(columnStatusIndex)
        val columnReasonIndex = cursor.getColumnIndex(DownloadManager.COLUMN_REASON)
        val reason = cursor.getInt(columnReasonIndex)
        val columnTitleIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TITLE)
        val name = cursor.getString(columnTitleIndex)
        var statusText = ""
        var reasonText = ""

        when (status) {
            DownloadManager.STATUS_FAILED -> {
                statusText = "STATUS_FAILED"
                when (reason) {
                    DownloadManager.ERROR_CANNOT_RESUME -> reasonText = "ERROR_CANNOT_RESUME"
                    DownloadManager.ERROR_DEVICE_NOT_FOUND -> reasonText = "ERROR_DEVICE_NOT_FOUND"
                    DownloadManager.ERROR_FILE_ALREADY_EXISTS -> reasonText =
                        "ERROR_FILE_ALREADY_EXISTS"
                    DownloadManager.ERROR_FILE_ERROR -> reasonText = "ERROR_FILE_ERROR"
                    DownloadManager.ERROR_HTTP_DATA_ERROR -> reasonText = "ERROR_HTTP_DATA_ERROR"
                    DownloadManager.ERROR_INSUFFICIENT_SPACE -> reasonText =
                        "ERROR_INSUFFICIENT_SPACE"
                    DownloadManager.ERROR_TOO_MANY_REDIRECTS -> reasonText =
                        "ERROR_TOO_MANY_REDIRECTS"
                    DownloadManager.ERROR_UNHANDLED_HTTP_CODE -> reasonText =
                        "ERROR_UNHANDLED_HTTP_CODE"
                    DownloadManager.ERROR_UNKNOWN -> reasonText = "ERROR_UNKNOWN"
                }
            }
            DownloadManager.STATUS_PAUSED -> {
                statusText = "STATUS_PAUSED"
                when (reason) {
                    DownloadManager.PAUSED_QUEUED_FOR_WIFI -> reasonText = "PAUSED_QUEUED_FOR_WIFI"
                    DownloadManager.PAUSED_UNKNOWN -> reasonText = "PAUSED_UNKNOWN"
                    DownloadManager.PAUSED_WAITING_FOR_NETWORK -> reasonText =
                        "PAUSED_WAITING_FOR_NETWORK"
                    DownloadManager.PAUSED_WAITING_TO_RETRY -> reasonText =
                        "PAUSED_WAITING_TO_RETRY"
                }
            }
            DownloadManager.STATUS_PENDING -> statusText = "STATUS_PENDING"
            DownloadManager.STATUS_RUNNING -> statusText = "STATUS_RUNNING"
            DownloadManager.STATUS_SUCCESSFUL -> {
                statusText = "STATUS_SUCCESSFUL"
            }
        }
        return DownloadInfo.HasInfo(name, statusText, reasonText)
    }

    @SuppressLint("Range")
    override fun downloadProgress(
        id: Long,
        progressHandle: (bytesDownloaded: Long, bytesTotal: Long) -> Unit
    ) {
        val query = DownloadManager.Query()
        query.setFilterById(id)
        var cursor = downloadManager.query(query)
        Thread {
            var preProgress = -1
            while (downloadIdList.find { it == id } != null && run {
                try {
                    cursor = downloadManager.query(query)
                    true
                } catch (e: Throwable) {
                    e.printStackTrace()
                    false
                }
                } && cursor.moveToFirst()) {
                val bytesDownloaded = cursor.getInt(
                    cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                )
                val bytesTotal = cursor.getInt(
                    cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                )
                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                    progressHandle(-1, -1)
                    removeDownloadProgress(id)
                    cursor.close()
                    break
                }
                if (preProgress != bytesDownloaded) {
                    if (bytesTotal != -1) { // Nếu chưa download xong
                        progressHandle(bytesDownloaded.toLong(), bytesTotal.toLong())
                    }
                    preProgress = bytesDownloaded
                }
                cursor.close()
            }
        }.start()
    }

    override fun removeDownloadProgress(id: Long) {
        downloadIdList.remove(id)
    }

    private val downloadIdList: MutableList<Long> by lazy {
        mutableListOf()
    }
}