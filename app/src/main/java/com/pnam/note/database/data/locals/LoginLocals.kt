package com.pnam.note.database.data.locals

import com.pnam.note.database.data.models.EmailPassword
import io.reactivex.rxjava3.core.Completable
import javax.inject.Singleton

@Singleton
interface LoginLocals {
    fun login(emailPassword: EmailPassword): Completable
    fun deleteLogin(email: String): Completable
    fun changePassword(email: String, newPassword: String): Completable
}