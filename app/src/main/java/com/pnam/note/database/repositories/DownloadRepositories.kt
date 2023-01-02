package com.pnam.note.database.repositories

import com.pnam.note.utils.DownloadInfo

interface DownloadRepositories {
    fun download(url: String): Long
    fun getDownloadStatus(id: Long): DownloadInfo
    fun downloadProgress(id: Long, progressHandle: (bytesDownloaded: Long, bytesTotal: Long) -> Unit)
}