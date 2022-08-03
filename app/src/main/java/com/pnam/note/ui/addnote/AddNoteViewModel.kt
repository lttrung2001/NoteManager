package com.pnam.note.ui.addnote

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pnam.note.database.data.models.Note
import com.pnam.note.throwable.NoConnectivityException
import com.pnam.note.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class AddNoteViewModel @Inject constructor(
    private val addNoteUseCase: AddNoteUseCase
) : ViewModel() {
    val internetError: MutableLiveData<String> by lazy {
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
            .subscribeOn(Schedulers.io())
            .subscribe(observer, this::addNoteError)
        composite.add(disposable)
    }

    private fun addNoteError(t: Throwable) {
        when (t) {
            is NoConnectivityException -> {
                internetError.postValue("")
            }
            else -> {
                addNote.postValue(Resource.Error(t.message ?: ""))
            }
        }
        t.printStackTrace()
    }
}