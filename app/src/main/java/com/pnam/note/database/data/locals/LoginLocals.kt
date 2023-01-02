package com.pnam.note.database.data.locals

import com.pnam.note.database.data.locals.entities.EmailPassword
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface LoginLocals {
    fun login(emailPassword: EmailPassword)
    fun deleteLogin(email: String)
    fun changePassword(email: String, newPassword: String)
    fun getSavedLogins(): Single<MutableList<EmailPassword>>
}