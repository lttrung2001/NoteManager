package com.pnam.note.ui.addimages

import android.content.Context
import com.pnam.note.database.data.models.PagingList
import com.pnam.note.database.repositories.ImageRepositories
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class AddNoteImagesUseCaseImpl @Inject constructor(
    private val imageRepositories: ImageRepositories
) : AddNoteImagesUseCase {
    override fun findImages(context: Context, page: Int, limit: Int): Single<PagingList<String>> {
        return imageRepositories.findImages(context, page, limit)
    }
}