package com.pnam.note.ui.login

import com.pnam.note.database.data.models.Login
import com.pnam.note.database.repositories.LoginRepositories
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class LoginUseCaseImpl @Inject constructor(private val repositories: LoginRepositories) : LoginUseCase {
    override fun login(email: String, password: String): Single<Login> =
        repositories.login(email, password).observeOn(Schedulers.io())
}