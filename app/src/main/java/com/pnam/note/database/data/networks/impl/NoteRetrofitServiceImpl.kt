package com.pnam.note.database.data.networks.impl

import com.pnam.note.database.data.models.APIResult
import com.pnam.note.database.data.models.Note
import com.pnam.note.database.data.models.PagingList
import com.pnam.note.database.data.networks.NoteNetworks
import com.pnam.note.utils.RetrofitUtils.SUCCESS
import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import retrofit2.http.*
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
        fun fetchNotes(@Query("limit") limit: Int): Single<Response<APIResult<List<Note>>>>

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

    override fun fetchNotes(limit: Int): Single<List<Note>> {
        return service.fetchNotes(limit).map {
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
        return service.addNote(note).map {
            if (it.code() == SUCCESS) {
                it.body()!!.data
            } else {
                throw Exception(it.message())
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