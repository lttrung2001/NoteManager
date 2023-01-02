package com.pnam.note.database.repositories

import android.content.Context
import com.pnam.note.database.data.locals.ImageLocals
import com.pnam.note.database.data.models.PagingList
import com.pnam.note.database.data.networks.ImageNetworks
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface ImageRepositories {
    val locals: ImageLocals
    val networks: ImageNetworks
    fun findImages(context: Context, page: Int, limit: Int): Single<PagingList<String>>
    fun deleteImage(url: String): Single<String>
}