package com.pnam.note.ui.imagedetail

import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface ImageDetailUseCase {
    fun getDownloadStatus(id: Long): String
    fun downloadProgress(
        id: Long,
        progressHandle: (bytesDownloaded: Long, bytesTotal: Long) -> Unit
    )
    fun download(url: String): Long

    fun deleteImage(url: String): Single<String>
}