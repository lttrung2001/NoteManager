package com.pnam.note.database.data.networks

import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface ImageNetworks {
    fun deleteImage(url: String): Single<String>
}