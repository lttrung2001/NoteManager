package com.pnam.note.database.data.networks.impl

import android.util.Base64
import com.pnam.note.database.data.models.APIResult
import com.pnam.note.database.data.networks.ImageNetworks
import com.pnam.note.utils.RetrofitUtils
import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Query
import javax.inject.Inject

class ImageRetrofitServiceImpl @Inject constructor(
    private val service: Service
) : ImageNetworks {
    override fun deleteImage(noteId: String, url: String): Single<String> {
//        val bytesArray = url.toByteArray(Charsets.UTF_8)
//        val encodedUrl = Base64.encodeToString(bytesArray, DEFAULT_BUFFER_SIZE)
        return service.deleteImage(noteId, url).map {
            if (it.code() == RetrofitUtils.SUCCESS) {
                it.body()!!.data
            } else {
                throw Exception(it.body()?.message)
            }
        }
    }

    interface Service {
        @DELETE("/delete-image")
        fun deleteImage(
            @Query("noteId") noteId: String,
            @Query("url") url: String
        ): Single<Response<APIResult<String>>>
    }
}