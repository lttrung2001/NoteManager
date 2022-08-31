package com.pnam.note.ui.addnoteimages

import com.pnam.note.database.repositories.NoteRepositories
import io.reactivex.rxjava3.core.Single
import java.io.File
import javax.inject.Inject

class AddNoteImagesUseCaseImpl @Inject constructor(
    private val noteRepositories: NoteRepositories
) : AddNoteImagesUseCase {
    override fun uploadImages(noteId: String, files: List<File>): Single<String> {
        return noteRepositories.uploadImages(noteId, files)
    }
}