package com.pnam.note.ui.login

import com.pnam.note.database.data.locals.entities.EmailPassword
import com.pnam.note.database.data.models.Login
import com.pnam.note.database.repositories.LoginRepositories
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject
class LoginUseCaseImpl @Inject constructor(private val repositories: LoginRepositories) :
    LoginUseCase {
    override fun login(email: String, password: String): Single<Login> {
        return repositories.login(email, password)
    }

    override fun deleteLogin(email: String) {
        return repositories.deleteLogin(email)
    }

    override fun getSavedLogins(): Single<MutableList<EmailPassword>> {
        return repositories.getSavedLogins()
    }
}