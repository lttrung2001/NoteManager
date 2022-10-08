package com.pnam.note.ui.addnoteimages

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pnam.note.database.data.models.PagingList
import com.pnam.note.utils.AppConstants.LIMIT_ON_PAGE
import com.pnam.note.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer
import javax.inject.Inject

@HiltViewModel
class AddNoteImagesViewModel @Inject constructor(
    private val useCase: AddNoteImagesUseCase
) : ViewModel() {
    private var page = 0
    private val _error: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    internal val error: MutableLiveData<String> get() = _error
    private val _imageListLiveData: MutableLiveData<Resource<PagingList<String>>> by lazy {
        MutableLiveData<Resource<PagingList<String>>>()
    }

    internal val imageListLiveData: MutableLiveData<Resource<PagingList<String>>> get() = _imageListLiveData

    private val composite: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    private var imageListDisposable: Disposable? = null

    private val imageListObserver: Consumer<PagingList<String>> by lazy {
        Consumer<PagingList<String>> { paging ->
            _imageListLiveData.postValue(Resource.Success(paging))
            page++
        }
    }

    internal fun findImages(context: Context) {
        _imageListLiveData.postValue(Resource.Loading())
        imageListDisposable?.let {
            composite.remove(it)
            it.dispose()
        }
        val limit = LIMIT_ON_PAGE

        imageListDisposable =
            useCase.findImages(context, page, limit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(imageListObserver) { t ->
                    _imageListLiveData.postValue(Resource.Error(t.message ?: "Unknown error"))
                }
    }
}