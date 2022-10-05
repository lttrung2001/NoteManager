package com.pnam.note.database.data.networks.impl

import android.os.FileUtils
import android.util.Log
import android.webkit.URLUtil
import com.google.gson.Gson
import com.pnam.note.database.data.models.APIResult
import com.pnam.note.database.data.models.Note
import com.pnam.note.database.data.models.PagingList
import com.pnam.note.database.data.networks.NoteNetworks
import com.pnam.note.utils.RetrofitUtils.SUCCESS
import io.reactivex.rxjava3.core.Single
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import retrofit2.http.*
import java.io.File
import javax.inject.Inject


class NoteRetrofitServiceImpl @Inject constructor(
    private val service: Service
) : NoteNetworks {
    interface Service {
        @GET("/get-notes")
        fun fetchNotes(
            @Query("page") page: Int,
            @Query("limit") limit: Int
        ): Single<Response<APIResult<PagingList<Note>>>>

        @GET("/refresh-notes")
        fun refreshNotes(
            @Query("page") page: Int,
            @Query("limit") limit: Int
        ): Single<Response<APIResult<PagingList<Note>>>>

        @GET("/get-note-detail")
        fun fetchNoteDetail(noteId: String): Single<Response<APIResult<Note>>>

        @Multipart
        @PUT("/add-note")
        fun addNote(
            @Part("note") note: RequestBody,
            @Part images: List<MultipartBody.Part>?
        ): Single<Response<APIResult<Note>>>

        @Multipart
        @POST("/edit-note")
        fun editNote(
            @Query("id") id: String,
            @Part("noteTitle") title: RequestBody,
            @Part("noteDescription") description: RequestBody,
            @Part images: List<MultipartBody.Part>?
        ): Single<Response<APIResult<Note>>>

        @DELETE("/delete-note")
        fun deleteNote(@Query("id") noteId: String): Single<Response<APIResult<Note>>>
    }

    override fun fetchNotes(page: Int, limit: Int): Single<PagingList<Note>> {
        return service.fetchNotes(page, limit).map {
            if (it.code() == SUCCESS) {
                it.body()!!.data
            } else {
                throw Exception(it.message())
            }
        }
    }

    override fun refreshNotes(page: Int, limit: Int): Single<PagingList<Note>> {
        return service.refreshNotes(page, limit).map {
            if (it.code() == SUCCESS) {
                it.body()!!.data
            } else {
                throw Exception(it.message())
            }
        }
    }

    override fun fetchNoteDetail(): Single<Note> {
        TODO("Not yet implemented")
    }

    override fun addNote(note: Note): Single<Note> {
        val parts = note.images?.map { path ->
            val file = File(path)
            val requestFile: RequestBody =
                file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("image", file.name, requestFile)
        }
        return service.addNote(RequestBody.create(MultipartBody.FORM, Gson().toJson(note)), parts)
            .map {
                if (it.code() == SUCCESS) {
                    it.body()!!.data
                } else {
                    throw Exception(it.message())
                }
            }
    }

    override fun editNote(note: Note): Single<Note> {
        val parts = note.images?.filter { imagePath ->
            !URLUtil.isNetworkUrl(imagePath)
        }?.map { imagePath ->
            val file = File(imagePath)
            val requestFile: RequestBody =
                file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("image", file.name, requestFile)
        }
        return service.editNote(
            note.id,
            RequestBody.create(MultipartBody.FORM, note.title),
            RequestBody.create(MultipartBody.FORM, note.description),
            parts
        ).map {
            if (it.code() == SUCCESS) {
                it.body()!!.data
            } else {
                throw Exception(it.message())
            }
        }
    }

    override fun deleteNote(id: String): Single<Note> {
        return service.deleteNote(id).map {
            if (it.code() == SUCCESS) {
                it.body()!!.data
            } else {
                throw Exception(it.message())
            }
        }
    }
}