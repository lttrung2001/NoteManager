package com.pnam.note.ui.addnoteimages

import android.content.ContentResolver
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pnam.note.database.data.models.PagingList
import com.pnam.note.utils.AppUtils.Companion.LIMIT_ON_PAGE
import com.pnam.note.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.File
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

    private val _imageLiveData: MutableLiveData<Resource<String>> by lazy {
        MutableLiveData<Resource<String>>()
    }
    internal val imageLiveData: MutableLiveData<Resource<String>> get() = _imageLiveData

    private val composite: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    private var imageListDisposable: Disposable? = null
    private var imageDisposable: Disposable? = null

    private val imageObserver: Consumer<String> by lazy {
        Consumer<String> { imagePath ->
            _imageLiveData.postValue(Resource.Success(imagePath))
        }
    }

    private val imageListObserver: Consumer<PagingList<String>> by lazy {
        Consumer<PagingList<String>> { paging ->
            _imageListLiveData.postValue(Resource.Success(paging))
            page++
        }
    }

    internal fun uploadNoteImages(noteId: String, imagesPath: List<String>) {
        _imageLiveData.postValue(Resource.Loading())
        imageDisposable?.let {
            composite.remove(it)
            it.dispose()
        }
        imageDisposable = useCase.uploadImages(noteId, imagesPath.map { path ->
            File(path)
        }).observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(imageObserver) { t ->
                t.printStackTrace()
            }
    }

    internal fun getLocalImages(context: Context) {
        _imageListLiveData.postValue(Resource.Loading())
        imageListDisposable?.let {
            composite.remove(it)
            it.dispose()
        }
        val limit = LIMIT_ON_PAGE
        val imageList: ArrayList<String> = ArrayList()
        val columns = arrayOf(
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media._ID
        )
        val sort = MediaStore.Images.ImageColumns.DATE_TAKEN
        val imageCursor = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, "$sort DESC LIMIT $limit OFFSET ${page * limit}"
            )
        } else {
            context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, Bundle().apply {
                    putInt(ContentResolver.QUERY_ARG_LIMIT, limit)
                    putInt(ContentResolver.QUERY_ARG_OFFSET, page * limit)
                }, null
            )
        }
        val nextPageCursor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, Bundle().apply {
                    putInt(ContentResolver.QUERY_ARG_LIMIT, limit)
                    putInt(ContentResolver.QUERY_ARG_OFFSET, (page + 1) * limit)
                }, null
            )
        } else {
            context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, "$sort DESC LIMIT $limit OFFSET ${(page + 1) * limit}"
            )
        }
        imageCursor?.let { cs ->
            for (i in 0 until cs.count) {
                cs.moveToPosition(i)
                val dataColumnIndex =
                    cs.getColumnIndex(MediaStore.Images.Media.DATA)
                imageList.add(cs.getString(dataColumnIndex))
            }
        }

        imageListDisposable =
            Single.just(PagingList(imageList, (nextPageCursor?.count ?: 0) > 0, page == 0))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(imageListObserver) { t ->
                    _imageListLiveData.postValue(Resource.Error(t.message ?: "Unknown error"))
                }
        imageCursor?.close()
        nextPageCursor?.close()
    }
}