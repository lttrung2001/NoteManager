package com.pnam.note.ui.login

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
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val useCase: LoginUseCase) : ViewModel() {
    val internetError: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    private val _login: MutableLiveData<Resource<Login>> by lazy {
        MutableLiveData<Resource<Login>>()
    }
    internal val login: MutableLiveData<Resource<Login>> get() = _login

    private val composite: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    private var loginDisposable: Disposable? = null
    private val observerLogin: Consumer<Login> by lazy {
        // Tim hieu Consumer trong RX
        Consumer<Login> { login ->
            _login.postValue(Resource.Success(login))
        }
    }

    internal fun login(email: String, password: String) {
        _login.postValue(Resource.Loading())
        loginDisposable?.let {
            composite.remove(it)
            it.dispose()
        }
        loginDisposable = useCase.login(email,password).observeOn(AndroidSchedulers.mainThread())
            .subscribe(observerLogin, this::loginError)
        composite.add(loginDisposable)
    }

    private fun loginError(t: Throwable) {
        when (t) {
            is NoConnectivityException -> {
                internetError.postValue("")
            }
            else -> {
                _login.postValue(Resource.Error(t.message ?: ""))
            }
        }
        t.printStackTrace()
    }

    override fun onCleared() {
        super.onCleared()
        composite.dispose()
    }
}