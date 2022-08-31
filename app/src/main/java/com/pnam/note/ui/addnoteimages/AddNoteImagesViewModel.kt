package com.pnam.note.ui.addnoteimages

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pnam.note.database.data.models.PagingList
import com.pnam.note.utils.AppUtils.Companion.LIMIT_ON_PAGE
import com.pnam.note.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddNoteImagesViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
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
            .subscribe(imageObserver) { t ->
                t.printStackTrace()
            }
    }

    internal fun getLocalImages() {
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
        val imageCursor: Cursor? = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
            null, "$sort DESC LIMIT $limit OFFSET ${page * limit}"
        )
        val nextPageCursor: Cursor? = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
            null, "$sort DESC LIMIT $limit OFFSET ${(page + 1) * limit}"
        )
        for (i in 0 until imageCursor!!.count) {
            imageCursor.moveToPosition(i)
            val dataColumnIndex =
                imageCursor.getColumnIndex(MediaStore.Images.Media.DATA)
            imageList.add(imageCursor.getString(dataColumnIndex))
        }

        imageListDisposable =
            Single.just(PagingList(imageList, (nextPageCursor?.count ?: 0) > 0, page == 0))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(imageListObserver) { t ->
                    _imageListLiveData.postValue(Resource.Error(t.message ?: "Unknown error"))
                }
    }
}