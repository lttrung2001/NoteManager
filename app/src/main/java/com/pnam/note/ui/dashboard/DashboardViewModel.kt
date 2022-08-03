package com.pnam.note.ui.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.room.rxjava3.RxRoom
import com.pnam.note.database.data.locals.dao.NoteDao
import com.pnam.note.database.data.models.Note
import com.pnam.note.database.data.models.PagingList
import com.pnam.note.database.data.networks.NoteNetworks
import com.pnam.note.throwable.NoConnectivityException
import com.pnam.note.utils.AppUtils.Companion.LIMIT_ON_PAGE
import com.pnam.note.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val useCase: DashboardUseCase,
    val noteDao: NoteDao,
    private val noteNetworks: NoteNetworks
) : ViewModel() {
    var page = 0
    val internetError: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    private val _dashboard: MutableLiveData<Resource<PagingList<Note>>> by lazy {
        MutableLiveData<Resource<PagingList<Note>>>()
    }
    private val _deleteNote: MutableLiveData<Resource<Note>> by lazy {
        MutableLiveData<Resource<Note>>()
    }
    private val _searchNotes: MutableLiveData<Resource<MutableList<Note>>> by lazy {
        MutableLiveData<Resource<MutableList<Note>>>()
    }

    internal val dashboard: MutableLiveData<Resource<PagingList<Note>>> get() = _dashboard
    internal val deleteNote: MutableLiveData<Resource<Note>> get() = _deleteNote
    internal val searchNotes: MutableLiveData<Resource<MutableList<Note>>> get() = _searchNotes

    private val composite: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    private var dashboardDisposable: Disposable? = null
    private var deleteNoteDisposable: Disposable? = null
    private var searchNotesDisposable: Disposable? = null

    private val observerDashboard: Consumer<PagingList<Note>> by lazy {
        Consumer<PagingList<Note>> { list ->
            _dashboard.postValue(Resource.Success(list))
        }
    }
    private val observerDeleteNote: Consumer<Note> by lazy {
        Consumer<Note> { note ->
            _deleteNote.postValue(Resource.Success(note))
        }
    }
    private val observerSearchNotes: Consumer<MutableList<Note>> by lazy {
        Consumer<MutableList<Note>> {
            _searchNotes.postValue(Resource.Success(it))
        }
    }

    internal fun getNotes() {
        _dashboard.postValue(Resource.Loading())
        dashboardDisposable?.let {
            composite.remove(it)
            it.dispose()
        }
        dashboardDisposable = useCase.getNotes(++page, LIMIT_ON_PAGE)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observerDashboard, this::loadNotesError)
        composite.add(dashboardDisposable)
    }

    internal fun deleteNote(note: Note) {
        deleteNoteDisposable?.let {
            composite.remove(it)
            it.dispose()
        }
        deleteNoteDisposable = useCase.deleteNote(note)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observerDeleteNote, this::deleteNoteError)
        composite.add(deleteNoteDisposable)
    }

    internal fun searchNotes(keySearch: String) {
        searchNotesDisposable?.let {
            composite.remove(it)
            it.dispose()
        }
        searchNotesDisposable = if (keySearch.isBlank()) {
            noteDao.findNotes(page, LIMIT_ON_PAGE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observerSearchNotes, this::loadNotesError)
        } else {
            useCase.searchNotes(keySearch)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observerSearchNotes, this::loadNotesError)
        }
    }

    private fun loadNotesError(t: Throwable) {
        page--
        when (t) {
            is NoConnectivityException -> {
                internetError.postValue("")
            }
            else -> {
                _dashboard.postValue(Resource.Error(t.message ?: ""))
            }
        }
        t.printStackTrace()
    }

    private fun deleteNoteError(t: Throwable) {
        when (t) {
            is NoConnectivityException -> {
                internetError.postValue("")
            }
            else -> {
                _deleteNote.postValue(Resource.Error(t.message ?: ""))
            }
        }
        t.printStackTrace()
    }

    override fun onCleared() {
        super.onCleared()
        composite.clear()
    }
}