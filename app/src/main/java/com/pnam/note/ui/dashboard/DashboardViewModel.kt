package com.pnam.note.ui.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pnam.note.database.data.models.Note
import com.pnam.note.database.data.models.PagingList
import com.pnam.note.throwable.NoConnectivityException
import com.pnam.note.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(private val useCase: DashboardUseCase) : ViewModel() {
    val internetError: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    private val _dashboard: MutableLiveData<Resource<PagingList<Note>>> by lazy {
        MutableLiveData<Resource<PagingList<Note>>>()
    }

    internal val dashboard: MutableLiveData<Resource<PagingList<Note>>> get() = _dashboard

    private val composite: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    private var dashboardDisposable: Disposable? = null
    private val observerDashboard: Consumer<PagingList<Note>> by lazy {
        // Tim hieu Consumer trong RX
        Consumer<PagingList<Note>> { list ->
            _dashboard.postValue(Resource.Success(list))
        }
    }

    internal fun getNotes(page: Int, limit: Int) {
        _dashboard.postValue(Resource.Loading())
        dashboardDisposable?.let {
            composite.remove(it)
            it.dispose()
        }
        dashboardDisposable = useCase.getNotes().observeOn(AndroidSchedulers.mainThread())
            .subscribe(observerDashboard, this::loadNotesError)
        composite.add(dashboardDisposable)
    }

    private fun loadNotesError(t: Throwable) {
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

    override fun onCleared() {
        super.onCleared()
        composite.dispose()
    }
}