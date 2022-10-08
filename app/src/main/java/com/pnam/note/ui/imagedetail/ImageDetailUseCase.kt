package com.pnam.note.ui.imagedetail

import javax.inject.Singleton

@Singleton
interface ImageDetailUseCase {
    fun getDownloadStatus(id: Long): String
    fun downloadProgress(
        id: Long,
        progressHandle: (bytesDownloaded: Long, bytesTotal: Long) -> Unit
    )
    fun removeDownloadProgress(id: Long)
    fun download(url: String): Long
}