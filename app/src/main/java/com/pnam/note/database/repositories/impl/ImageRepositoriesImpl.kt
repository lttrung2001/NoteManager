package com.pnam.note.database.repositories.impl

import android.content.Context
import android.webkit.URLUtil
import com.pnam.note.database.data.locals.ImageLocals
import com.pnam.note.database.data.models.PagingList
import com.pnam.note.database.data.networks.ImageNetworks
import com.pnam.note.database.repositories.ImageRepositories
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class ImageRepositoriesImpl @Inject constructor(
    override val locals: ImageLocals,
    override val networks: ImageNetworks
) : ImageRepositories {
    override fun findImages(context: Context, page: Int, limit: Int): Single<PagingList<String>> {
        return locals.findImages(context, page, limit)
    }

    override fun deleteImage(url: String): Single<String> {
        return networks.deleteImage(url)
    }
}