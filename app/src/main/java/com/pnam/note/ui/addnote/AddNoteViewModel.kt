package com.pnam.note.ui.addnote

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pnam.note.database.data.locals.NoteLocals
import com.pnam.note.database.data.models.Note
import com.pnam.note.throwable.NoConnectivityException
import com.pnam.note.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddNoteViewModel @Inject constructor(
    private val addNoteUseCase: AddNoteUseCase,
    private val noteLocals: NoteLocals
) : ViewModel() {
    val error: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    internal val addNote: MutableLiveData<Resource<Note>> by lazy {
        MutableLiveData<Resource<Note>>()
    }
    private val composite: CompositeDisposable by lazy {
        CompositeDisposable()
    }
    private var disposable: Disposable? = null
    private val observer: Consumer<Note> by lazy {
        // Tim hieu Consumer trong RX
        Consumer<Note> { note ->
            addNote.postValue(Resource.Success(note))
        }
    }

    internal fun addNote(note: Note) {
        addNote.postValue(Resource.Loading())
        disposable?.let {
            composite.remove(it)
            it.dispose()
        }
        disposable = addNoteUseCase.addNote(note).observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer) { t ->
                when (t) {
                    is NoConnectivityException -> {
                        // Cần phát ra item đã lưu trong local để cập nhật lên giao diện
                        viewModelScope.launch(Dispatchers.IO) {
                            when (noteLocals.addNoteOffline(note)) {
                                null -> {
                                    addNote.postValue(Resource.Error(t.message))
                                }
                                else -> {
                                    disposable = noteLocals.findNoteDetail(note.id)
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(observer) { localError ->
                                            addNote.postValue(
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
                        addNote.postValue(Resource.Error(t.message ?: "Unknown error"))
                    }
                }
                t.printStackTrace()
            }
        disposable?.let { composite.add(it) }
    }
}