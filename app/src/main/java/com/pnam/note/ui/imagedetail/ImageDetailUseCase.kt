package com.pnam.note.ui.imagedetail

import io.reactivex.rxjava3.core.Single

interface ImageDetailUseCase {
    fun delete(path: String): Single<String>
    fun download(path: String): Single<String>
}