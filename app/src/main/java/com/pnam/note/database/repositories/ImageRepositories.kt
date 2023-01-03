package com.pnam.note.database.repositories

import android.content.Context
import com.pnam.note.database.data.locals.ImageLocals
import com.pnam.note.database.data.models.PagingList
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface ImageRepositories {
    val locals: ImageLocals
    fun findImages(context: Context, page: Int, limit: Int): Single<PagingList<String>>
    fun deleteImage(noteId: String, url: String): Single<String>
}