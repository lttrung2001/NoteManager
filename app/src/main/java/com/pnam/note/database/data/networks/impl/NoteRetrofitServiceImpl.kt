package com.pnam.note.database.data.networks.impl

import com.pnam.note.database.data.models.APIResult
import com.pnam.note.database.data.models.Note
import com.pnam.note.database.data.models.PagingList
import com.pnam.note.database.data.networks.NoteNetworks
import com.pnam.note.throwable.NotFoundException
import com.pnam.note.utils.RetrofitUtils.NOT_FOUND
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
        fun fetchNotes(): Single<Response<APIResult<PagingList<Note>>>>

        @GET("get-note-detail")
        fun fetchNoteDetail(noteId: String): Single<Response<APIResult<Note>>>

        @PUT("/add-note")
        fun addNote(@Body note: Note): Single<Response<APIResult<Note>>>

        @POST("/edit-note")
        fun editNote(@Body note: Note): Single<Response<APIResult<Note>>>

        @DELETE("/delete-note")
        fun deleteNote(@Body note: Note): Single<Response<APIResult<Note>>>
    }

    override fun fetchNotes(): Single<PagingList<Note>> {
        return service.fetchNotes().map {
            if (it.code() == NOT_FOUND) {
                throw NotFoundException()
            } else {
                it.body()!!.data
            }
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