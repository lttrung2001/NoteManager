package com.pnam.note.ui.imagedetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pnam.note.throwable.NoConnectivityException
import com.pnam.note.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer
import javax.inject.Inject

@HiltViewModel
class ImageDetailViewModel @Inject constructor(
    private val useCase: ImageDetailUseCase
) : ViewModel() {
    private val _downloadIdValue: Long get() = _downloadId.value ?: -1
    private val _downloadId: MutableLiveData<Long> by lazy {
        MutableLiveData<Long>()
    }
    private val _deleteImageLiveData: MutableLiveData<Resource<String>> by lazy {
        MutableLiveData<Resource<String>>()
    }

    internal val deleteImageLiveData: MutableLiveData<Resource<String>> get() = _deleteImageLiveData

    private val composite: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    private val deleteImageObserver: Consumer<String> by lazy {
        Consumer<String> { str ->
            _deleteImageLiveData.postValue(Resource.Success(str))
        }
    }

    private var deleteImageDisposable: Disposable? = null

    internal fun deleteImage(url: String) {
        _deleteImageLiveData.postValue(Resource.Loading())
        deleteImageDisposable?.let {
            composite.remove(it)
            it.dispose()
        }
        deleteImageDisposable = useCase.deleteImage(url)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(deleteImageObserver) { t ->
                when (t) {
                    is NoConnectivityException -> {
                        _deleteImageLiveData.postValue(Resource.Error(t.message))
                    }
                    else -> {
                        _deleteImageLiveData.postValue(Resource.Error(t.message ?: "Unknown error"))
                    }
                }
            }
    }

    internal fun download(url: String): Long {
        return useCase.download(url)
    }

    internal fun getDownloadStatus(id: Long): String {
        return useCase.getDownloadStatus(_downloadIdValue)
    }

    internal fun downloadProgress(
        id: Long,
        progressHandle: (bytesDownloaded: Long, bytesTotal: Long) -> Unit
    ) {
        useCase.downloadProgress(id, progressHandle)
    }
}