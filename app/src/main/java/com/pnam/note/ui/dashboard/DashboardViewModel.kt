package com.pnam.note.ui.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pnam.note.database.data.locals.NoteLocals
import com.pnam.note.database.data.models.Note
import com.pnam.note.database.data.models.NoteStatus
import com.pnam.note.database.data.models.PagingList
import com.pnam.note.throwable.NoConnectivityException
import com.pnam.note.utils.AppUtils.Companion.LIMIT_ON_PAGE
import com.pnam.note.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val useCase: DashboardUseCase,
    val noteLocals: NoteLocals
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
        Consumer<MutableList<Note>> { list ->
            _searchNotes.postValue(Resource.Success(list))
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
            .subscribe(observerDashboard) { t ->
                when (t) {
                    is NoConnectivityException -> {
                        viewModelScope.launch(Dispatchers.IO) {
                            dashboardDisposable =
                                noteLocals.findNotes(page, LIMIT_ON_PAGE).map { localNotes ->
                                    PagingList(localNotes, hasNextPage = true, hasPrePage = false)
                                }
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(observerDashboard)
                        }
                    }
                    else -> {
                        page--
                        _dashboard.postValue(Resource.Error(t.message ?: ""))
                    }
                }
                t.printStackTrace()
            }
        composite.add(dashboardDisposable)
    }

    internal fun deleteNote(note: Note) {
        deleteNoteDisposable?.let {
            composite.remove(it)
            it.dispose()
        }
        deleteNoteDisposable = useCase.deleteNote(note)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observerDeleteNote) { t ->
                when (t) {
                    is NoConnectivityException -> {
                        viewModelScope.launch(Dispatchers.IO) {
                            deleteNoteDisposable = noteLocals.findNoteDetail(note.id)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(observerDeleteNote) {
                                    _deleteNote.postValue(Resource.Error(t.message ?: ""))
                                }
                            noteLocals.addNoteStatus(NoteStatus(note.id,3))
                            noteLocals.deleteNote(note)
                        }
                    }
                    else -> {
                        _deleteNote.postValue(Resource.Error(t.message ?: ""))
                    }
                }
                t.printStackTrace()
            }
        composite.add(deleteNoteDisposable)
    }

    internal fun searchNotes(keySearch: String) {
        searchNotesDisposable?.let {
            composite.remove(it)
            it.dispose()
        }
        searchNotesDisposable = if (keySearch.isBlank()) {
            noteLocals.findNotes(page, LIMIT_ON_PAGE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(observerSearchNotes) {
                    _searchNotes.postValue(Resource.Error(it.message ?: ""))
                }
        } else {
            useCase.searchNotes(keySearch)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(observerSearchNotes) {
                    _searchNotes.postValue(Resource.Error(it.message ?: ""))
                }
        }
        composite.add(searchNotesDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        composite.clear()
    }
}