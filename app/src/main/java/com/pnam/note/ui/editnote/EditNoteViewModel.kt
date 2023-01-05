package com.pnam.note.ui.editnote

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pnam.note.database.data.locals.NoteLocals
import com.pnam.note.database.data.locals.entities.Note
import com.pnam.note.throwable.NoConnectivityException
import com.pnam.note.ui.notedetail.NoteDetailUseCase
import com.pnam.note.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditNoteViewModel @Inject constructor(
    private val editNoteUseCase: EditNoteUseCase,
    private val noteDetailUseCase: NoteDetailUseCase,
    private val noteLocals: NoteLocals
) : ViewModel() {
    val error: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    internal val editNote: MutableLiveData<Resource<Note>> by lazy {
        MutableLiveData<Resource<Note>>()
    }
    internal val imagesLiveData: MutableLiveData<Resource<List<String>>> by lazy {
        MutableLiveData<Resource<List<String>>>()
    }
    private val _getNoteDetailLiveData: MutableLiveData<Resource<Note>> by lazy {
        MutableLiveData<Resource<Note>>()
    }

    internal val getNoteDetailLiveData: MutableLiveData<Resource<Note>> get() = _getNoteDetailLiveData

    private val composite: CompositeDisposable by lazy {
        CompositeDisposable()
    }
    private var disposable: Disposable? = null
    private var imagesDisposable: Disposable? = null
    private var getNoteDetailDisposable: Disposable? = null
    private var loadImagesDisposable: Disposable? = null

    private val observer: Consumer<Note> by lazy {
        Consumer<Note> { note ->
            editNote.postValue(Resource.Success(note))
        }
    }
    private val imagesObserver: Consumer<List<String>> by lazy {
        Consumer<List<String>> { list ->
            imagesLiveData.postValue(Resource.Success(list))
        }
    }
    private val observerGetNoteDetail: Consumer<Note> by lazy {
        Consumer<Note> { note ->
            _getNoteDetailLiveData.postValue(Resource.Success(note))
        }
    }

    internal fun editNote(note: Note) {
        editNote.postValue(Resource.Loading())
        disposable?.let {
            composite.remove(it)
            it.dispose()
        }
        disposable = editNoteUseCase.editNote(note)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer) { t ->
                when (t) {
                    is NoConnectivityException -> {
                        viewModelScope.launch(Dispatchers.IO) {
                            when (noteLocals.editNoteOffline(note)) {
                                null -> {
                                    editNote.postValue(Resource.Error(t.message))
                                }
                                else -> {
                                    disposable = noteLocals.findNoteDetail(note.id)
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(observer) { localError ->
                                            editNote.postValue(
                                                Resource.Error(
                                                    localError.message ?: "Unknown error"
                                                )
                                            )
                                        }
                                }
                            }

                        }
                    }
                    else -> {
                        editNote.postValue(Resource.Error(t.message ?: "Unknown error"))
                    }
                }
            }
        disposable?.let { composite.add(it) }
    }

    internal fun getNoteDetail(id: String) {
        _getNoteDetailLiveData.postValue(Resource.Loading())
        getNoteDetailDisposable?.let {
            composite.remove(it)
            it.dispose()
        }
        getNoteDetailDisposable = noteDetailUseCase.getNoteDetail(id)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observerGetNoteDetail) { t ->
                when (t) {
                    is NoConnectivityException -> {
                        viewModelScope.launch(Dispatchers.IO) {
                            getNoteDetailDisposable = noteLocals.findNoteDetail(id)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(observerGetNoteDetail) { localError ->
                                    _getNoteDetailLiveData.postValue(
                                        Resource.Error(
                                            localError.message ?: "Unknown error"
                                        )
                                    )
                                }
                        }
                    }
                    else -> {
                        _getNoteDetailLiveData.postValue(
                            Resource.Error(
                                t.message ?: "Unknown error"
                            )
                        )
                    }
                }
            }
    }

    internal fun addImages(images: List<String>) {
        imagesLiveData.postValue(Resource.Loading())
        imagesDisposable?.let {
            composite.remove(it)
            it.dispose()
        }
        imagesDisposable = Single.just(images)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(imagesObserver) { t ->
                imagesLiveData.postValue(Resource.Error(t.message ?: "Unknown error"))
            }
    }
}