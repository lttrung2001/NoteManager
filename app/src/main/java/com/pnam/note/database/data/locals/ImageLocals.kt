package com.pnam.note.database.data.locals

import android.content.Context
import com.pnam.note.database.data.models.PagingList
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface ImageLocals {
    fun findImages(context: Context, page: Int, limit: Int): Single<PagingList<String>>
}