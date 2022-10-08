package com.pnam.note.ui.imagedetail

import com.pnam.note.database.repositories.DownloadRepositories
import javax.inject.Inject

class ImageDetailUseCaseImpl @Inject constructor(
    private val repositories: DownloadRepositories
) : ImageDetailUseCase {
    override fun getDownloadStatus(id: Long): String {
        return repositories.getDownloadStatus(id).toString()
    }

    override fun downloadProgress(
        id: Long,
        progressHandle: (bytesDownloaded: Long, bytesTotal: Long) -> Unit
    ) {
        return repositories.downloadProgress(id, progressHandle)
    }

    override fun removeDownloadProgress(id: Long) {
        return repositories.removeDownloadProgress(id)
    }

    override fun download(url: String): Long {
        return repositories.download(url)
    }

}