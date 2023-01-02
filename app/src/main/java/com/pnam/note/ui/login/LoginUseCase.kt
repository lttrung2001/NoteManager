package com.pnam.note.ui.login

import com.pnam.note.database.data.locals.entities.EmailPassword
import com.pnam.note.database.data.models.Login
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface LoginUseCase {
    fun login(email: String, password: String): Single<Login>
    fun deleteLogin(email: String)
    fun getSavedLogins(): Single<MutableList<EmailPassword>>
}