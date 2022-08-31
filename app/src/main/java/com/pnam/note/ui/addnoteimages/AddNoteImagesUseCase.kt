package com.pnam.note.ui.addnoteimages

import io.reactivex.rxjava3.core.Single
import java.io.File
import javax.inject.Singleton

@Singleton
interface AddNoteImagesUseCase {
    fun uploadImages(noteId: String, files: List<File>): Single<String>
}