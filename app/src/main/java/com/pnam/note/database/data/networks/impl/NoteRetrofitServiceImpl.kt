package com.pnam.note.database.data.networks.impl

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
        ): Single<Response<APIResult<List<Note>>>>

        @GET("/get-note-detail")
        fun fetchNoteDetail(noteId: String): Single<Response<APIResult<Note>>>

        @PUT("/add-note")
        fun addNote(@Body note: Note): Single<Response<APIResult<Note>>>

        @POST("/edit-note")
        fun editNote(
            @Query("id") id: String,
            @Body map: Map<String, String>
        ): Single<Response<APIResult<Note>>>

        @DELETE("/delete-note")
        fun deleteNote(@Query("id") noteId: String): Single<Response<APIResult<Note>>>

        @Multipart
        @POST("/upload-note-images")
        fun uploadNoteImages(
            @Query("id") noteId: String,
            @Part image: MultipartBody.Part
        ):
                Single<Response<APIResult<String>>>
    }

    override fun fetchNotes(page: Int, limit: Int): Single<PagingList<Note>> {
        return service.fetchNotes(page, limit).map {
            if (it.code() == SUCCESS) {
                it.body()!!.data
            } else {
                throw Exception(it.body()!!.message)
            }
        }
    }

    override fun refreshNotes(page: Int, limit: Int): Single<List<Note>> {
        return service.refreshNotes(page, limit).map {
            if (it.code() == SUCCESS) {
                it.body()!!.data
            } else {
                throw Exception(it.body()!!.message)
            }
        }
    }

    override fun fetchNoteDetail(): Single<Note> {
        TODO("Not yet implemented")
    }

    override fun addNote(note: Note): Single<Note> {
        return service.addNote(note).map {
            if (it.code() == SUCCESS) {
                it.body()!!.data
            } else {
                throw Exception(it.body()!!.message)
            }
        }
    }

    override fun editNote(note: Note): Single<Note> {
        val body = HashMap<String, String>()
        body["title"] = note.title
        body["description"] = note.description
        return service.editNote(note.id, body).map {
            if (it.code() == SUCCESS) {
                it.body()!!.data
            } else {
                throw Exception(it.body()!!.message)
            }
        }
    }

    override fun deleteNote(id: String): Single<Note> {
        return service.deleteNote(id).map {
            if (it.code() == SUCCESS) {
                it.body()!!.data
            } else {
                throw Exception(it.body()!!.message)
            }
        }
    }

    override fun uploadImages(noteId: String, files: List<File>): Single<String> {
        val parts = files.map { file ->
            val requestFile: RequestBody =
                file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("image", file.name, requestFile)
        }
        return service.uploadNoteImages(noteId, parts[0]).map {
            if (it.code() == SUCCESS) {
                it.body()!!.data
            } else {
                throw Exception(it.body()!!.message)
            }
        }
    }
}