package com.pnam.note.database.data.networks.impl

import android.util.Log
import com.google.gson.Gson
import com.pnam.note.database.data.models.APIResult
import com.pnam.note.database.data.models.Note
import com.pnam.note.database.data.models.PagingList
import com.pnam.note.database.data.networks.NoteNetworks
import com.pnam.note.throwable.NotFoundException
import com.pnam.note.utils.RetrofitUtils.NOT_FOUND
import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

class NoteRetrofitServiceImpl(
    private val service: Service
) : NoteNetworks {
    interface Service {
        @GET("/fetch-notes")
        fun fetchNotes(): Single<Response<PagingList<Note>>>
        @GET("fetch-note-detail")
        fun fetchNoteDetail(noteId: String): Single<Response<APIResult>>
        @PUT("/add-note")
        fun addNote(note: Note): Single<Response<APIResult>>
        @POST("/edit-note")
        fun editNote(note: Note): Single<Response<Note>>
        @DELETE("/delete-note")
        fun deleteNote(note: Note): Single<Response<Note>>
    }
    override fun fetchNotes(): Single<PagingList<Note>> =
         service.fetchNotes().map {
            if (it.code() == NOT_FOUND) {
                throw NotFoundException()
            } else {
                Log.d("test",it.body()!!.data.toString())
                it.body()!!
            }
        }

    override fun fetchNoteDetail(): Single<Note> {
        TODO("Not yet implemented")
    }

    override fun addNote(note: Note): Single<Note> {
        TODO("Not yet implemented")
    }

    override fun editNote(note: Note): Single<Note> {
        TODO("Not yet implemented")
    }

    override fun deleteNote(note: Note): Single<Note> {
        TODO("Not yet implemented")
    }
}