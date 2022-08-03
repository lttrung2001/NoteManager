package com.pnam.note.ui.register

import com.pnam.note.database.data.models.Login
import com.pnam.note.database.repositories.LoginRepositories
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class RegisterUseCaseImpl @Inject constructor(private val repositories: LoginRepositories) :
    RegisterUseCase {
    override fun register(email: String, password: String): Single<Login> {
        return repositories.register(email, password)
    }
}