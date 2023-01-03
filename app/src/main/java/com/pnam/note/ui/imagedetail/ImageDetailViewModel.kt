package com.pnam.note.ui.imagedetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ImageDetailViewModel @Inject constructor(
    private val useCase: ImageDetailUseCase
) : ViewModel() {
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

    internal fun deleteImage(noteId: String, url: String) {
        _deleteImageLiveData.postValue(Resource.Loading())
        deleteImageDisposable?.let {
            composite.remove(it)
            it.dispose()
        }
        deleteImageDisposable = useCase.deleteImage(noteId, url)
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

    fun download(url: String): Long {
        return useCase.download(url)
    }

    fun getDownloadStatus(id: Long): String {
        return useCase.getDownloadStatus(_downloadIdValue)
    }

    fun downloadProgress(
        id: Long,
        progressHandle: (bytesDownloaded: Long, bytesTotal: Long) -> Unit
    ) {
        useCase.downloadProgress(id, progressHandle)
    }
}