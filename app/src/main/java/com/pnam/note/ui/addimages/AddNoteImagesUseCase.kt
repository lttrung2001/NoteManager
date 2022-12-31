package com.pnam.note.ui.addimages

import android.content.Context
import com.pnam.note.database.data.models.PagingList
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface AddNoteImagesUseCase {
    fun findImages(context: Context, page: Int, limit: Int): Single<PagingList<String>>
}