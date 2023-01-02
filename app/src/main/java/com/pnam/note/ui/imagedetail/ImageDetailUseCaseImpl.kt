package com.pnam.note.ui.imagedetail

import com.pnam.note.database.repositories.DownloadRepositories
import com.pnam.note.database.repositories.ImageRepositories
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class ImageDetailUseCaseImpl @Inject constructor(
    private val repositories: DownloadRepositories,
    private val imageRepositories: ImageRepositories
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

    override fun download(url: String): Long {
        return repositories.download(url)
    }

    override fun deleteImage(url: String): Single<String> {
        return imageRepositories.deleteImage(url)
    }

}