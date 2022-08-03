package com.pnam.note.ui.editnote

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
import javax.inject.Inject

@HiltViewModel
class EditNoteViewModel @Inject constructor(
    private val editNoteUseCase: EditNoteUseCase
) : ViewModel() {
    val internetError: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    internal val editNote: MutableLiveData<Resource<Note>> by lazy {
        MutableLiveData<Resource<Note>>()
    }

    private val composite: CompositeDisposable by lazy {
        CompositeDisposable()
    }
    private var disposable: Disposable? = null
    private val observer: Consumer<Note> by lazy {
        Consumer<Note> { note ->
            editNote.postValue(Resource.Success(note))
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
            .subscribe(observer, this::editNoteError)
        composite.add(disposable)
    }

    private fun editNoteError(t: Throwable) {
        when (t) {
            is NoConnectivityException -> {
                internetError.postValue("No network connection")
            }
            else -> {
                editNote.postValue(Resource.Error(t.message ?: ""))
            }
        }
        t.printStackTrace()
    }
}