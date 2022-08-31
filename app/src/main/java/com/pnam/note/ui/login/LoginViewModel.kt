package com.pnam.note.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pnam.note.database.data.locals.dao.LoginDao
import com.pnam.note.database.data.models.EmailPassword
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
class LoginViewModel @Inject constructor(
    private val useCase: LoginUseCase,
    val loginDao: LoginDao
) : ViewModel() {
    val error: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    private val _login: MutableLiveData<Resource<Login>> by lazy {
        MutableLiveData<Resource<Login>>()
    }

    internal val login: MutableLiveData<Resource<Login>> get() = _login

    private val _savedLogin: MutableLiveData<Resource<MutableList<EmailPassword>>> by lazy {
        MutableLiveData<Resource<MutableList<EmailPassword>>>()
    }

    internal val savedLogin: MutableLiveData<Resource<MutableList<EmailPassword>>> get() = _savedLogin

    private val composite: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    private var loginDisposable: Disposable? = null
    private var savedLoginDisposable: Disposable? = null

    private val observerLogin: Consumer<Login> by lazy {
        // Tim hieu Consumer trong RX
        Consumer<Login> { login ->
            _login.postValue(Resource.Success(login))
        }
    }
    private val observerSavedLogin: Consumer<MutableList<EmailPassword>> by lazy {
        Consumer<MutableList<EmailPassword>> { list ->
            _savedLogin.postValue(Resource.Success(list))
        }
    }

    internal fun login(email: String, password: String) {
        _login.postValue(Resource.Loading())
        loginDisposable?.let {
            composite.remove(it)
            it.dispose()
        }
        loginDisposable = useCase
            .login(email, password)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observerLogin, this::loginError)
        loginDisposable?.let { composite.add(it) }
    }

    internal fun getSavedLogin() {
        _savedLogin.postValue(Resource.Loading())
        savedLoginDisposable?.let {
            composite.remove(it)
            it.dispose()
        }
        savedLoginDisposable = loginDao.getAllLogin()
            .subscribe(observerSavedLogin) {
                _savedLogin.postValue(Resource.Error(it.message ?: "Unknown error"))
            }
        savedLoginDisposable?.let { composite.add(it) }
    }

    private fun loginError(t: Throwable) {
        when (t) {
            is NoConnectivityException -> {
                error.postValue("No internet connection")
            }
            else -> {
                _login.postValue(Resource.Error(t.message?: "Unknown error"))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        composite.dispose()
    }
}