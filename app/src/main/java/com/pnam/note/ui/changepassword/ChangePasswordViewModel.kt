package com.pnam.note.ui.changepassword

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pnam.note.database.data.models.Login
import com.pnam.note.throwable.NoConnectivityException
import com.pnam.note.ui.login.LoginActivity
import com.pnam.note.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val useCase: ChangePasswordUseCase,
    private val sp: SharedPreferences
) : ViewModel() {
    val error: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    private val _changePassword: MutableLiveData<Resource<Login>> by lazy {
        MutableLiveData<Resource<Login>>()
    }

    internal val changePassword: MutableLiveData<Resource<Login>> get() = _changePassword

    private val composite: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    private var changePasswordDisposable: Disposable? = null

    private val changePasswordObserver: Consumer<Login> by lazy {
        Consumer<Login> {
            _changePassword.postValue(Resource.Success(it))
        }
    }

    internal fun changePassword(oldPassword: String, newPassword: String) {
        _changePassword.postValue(Resource.Loading())
        changePasswordDisposable?.let {
            composite.remove(it)
            it.dispose()
        }
        changePasswordDisposable = sp.getString(LoginActivity.EMAIL, "")?.let {
            useCase.changePassword(it, oldPassword, newPassword)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(changePasswordObserver) { t ->
                    when (t) {
                        is NoConnectivityException -> {
                            error.postValue("No internet connection")
                        }
                        else -> {
                            _changePassword.postValue(Resource.Error(t.message?: "Unknown error"))
                        }
                    }
                }
        }
    }
}