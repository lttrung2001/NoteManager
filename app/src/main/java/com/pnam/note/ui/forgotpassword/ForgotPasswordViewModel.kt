package com.pnam.note.ui.forgotpassword

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
class ForgotPasswordViewModel @Inject constructor(
    private val useCase: ForgotPasswordUseCase
) : ViewModel() {
    val error: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    private val _forgotPassword: MutableLiveData<Resource<Unit>> by lazy {
        MutableLiveData<Resource<Unit>>()
    }

    internal val forgotPassword: MutableLiveData<Resource<Unit>> get() = _forgotPassword

    private val composite: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    private var forgotPasswordDisposable: Disposable? = null

    private val forgotPasswordObserver: Consumer<Unit> by lazy {
        Consumer<Unit> {
            _forgotPassword.postValue(Resource.Success(it))
        }
    }

    internal fun forgotPassword(email: String) {
        _forgotPassword.postValue(Resource.Loading())
        forgotPasswordDisposable?.let {
            composite.remove(it)
            composite.dispose()
        }
        forgotPasswordDisposable = useCase.forgotPassword(email)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(forgotPasswordObserver) { t ->
                when (t) {
                    is NoConnectivityException -> {
                        error.postValue("No internet connection")
                    }
                    else -> {
                        _forgotPassword.postValue(Resource.Error(t.message?: "Unknown error"))
                    }
                }
            }
    }
}