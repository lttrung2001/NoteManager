package com.pnam.note.ui.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pnam.note.database.data.models.Login
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
class RegisterViewModel @Inject constructor(private val useCase: RegisterUseCase) : ViewModel() {
    val error: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    private val _register: MutableLiveData<Resource<Login>> by lazy {
        MutableLiveData<Resource<Login>>()
    }
    internal val register: MutableLiveData<Resource<Login>> get() = _register

    private val composite: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    private var registerDisposable: Disposable? = null
    private val observerLogin: Consumer<Login> by lazy {
        // Tim hieu Consumer trong RX
        Consumer<Login> { login ->
            _register.postValue(Resource.Success(login))
        }
    }

    internal fun register(email: String, password: String) {
        _register.postValue(Resource.Loading())
        registerDisposable?.let {
            composite.remove(it)
            it.dispose()
        }
        registerDisposable = useCase
            .register(email, password)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observerLogin, this::registerError)
        registerDisposable?.let { composite.add(it) }
    }

    private fun registerError(t: Throwable) {
        when (t) {
            is NoConnectivityException -> {
                error.postValue("")
            }
            else -> {
                _register.postValue(Resource.Error(t.message?: "Unknown error"))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        composite.dispose()
    }
}